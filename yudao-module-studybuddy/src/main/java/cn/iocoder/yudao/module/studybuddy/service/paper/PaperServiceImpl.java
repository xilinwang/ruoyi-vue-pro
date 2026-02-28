package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.*;
import cn.iocoder.yudao.module.studybuddy.convert.paper.PaperConvert;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperDO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.QuestionDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.PaperMapper;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.QuestionMapper;
import cn.iocoder.yudao.module.studybuddy.enums.paper.PaperStatusEnum;
import cn.iocoder.yudao.module.studybuddy.service.paper.event.PaperAnalyzeEvent;
import cn.iocoder.yudao.module.studybuddy.service.paper.event.PaperOcrEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.studybuddy.enums.ErrorCodeConstants.*;

/**
 * 试卷 Service 实现类
 *
 * @author StudyBuddy
 */
@Service
@Validated
@Slf4j
public class PaperServiceImpl implements PaperService {

    @Resource
    private PaperMapper paperMapper;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private PaperFileService paperFileService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPaper(PaperCreateReqVO createReqVO) {
        // 自动生成试卷编号（如果未提供）
        String paperNo = createReqVO.getPaperNo();
        if (paperNo == null || paperNo.trim().isEmpty()) {
            paperNo = generatePaperNo(createReqVO.getSubject(), createReqVO.getTitle());
        } else {
            // 校验试卷编号唯一性
            validatePaperNoUnique(null, paperNo);
        }

        // 默认学生ID（如果未提供，默认为1即管理员）
        Long studentId = createReqVO.getStudentId();
        if (studentId == null) {
            studentId = 1L;
        }

        // 插入数据库
        PaperDO paper = PaperDO.builder()
                .paperNo(paperNo)
                .studentId(studentId)
                .subjectId(createReqVO.getSubjectId())
                .subject(createReqVO.getSubject())
                .title(createReqVO.getTitle())
                .examDate(createReqVO.getExamDate())
                .grade(createReqVO.getGrade())
                .semester(createReqVO.getSemester())
                .filePath(createReqVO.getFilePath())
                .status(PaperStatusEnum.OCR_PROCESSING.getCode())
                .build();
        paperMapper.insert(paper);

        // 处理多文件上传
        if (createReqVO.getFiles() != null && !createReqVO.getFiles().isEmpty()) {
            paperFileService.batchCreatePaperFiles(paper.getId(), createReqVO.getFiles());
            // 使用第一个文件路径触发OCR（保持向后兼容）
            String firstFilePath = createReqVO.getFiles().get(0).getFilePath();
            eventPublisher.publishEvent(new PaperOcrEvent(paper.getId(), firstFilePath));
        } else if (paper.getFilePath() != null && !paper.getFilePath().trim().isEmpty()) {
            // 兼容旧的单文件上传方式
            eventPublisher.publishEvent(new PaperOcrEvent(paper.getId(), paper.getFilePath()));
        }

        log.info("[createPaper] 创建试卷成功，试卷ID: {}, 试卷编号: {}", paper.getId(), paper.getPaperNo());
        return paper.getId();
    }

    /**
     * 生成试卷编号
     * 格式: 科目拼音首字母 + 年月日 + 4位序号
     * 例如: MATH20250127001
     */
    private String generatePaperNo(String subject, String title) {
        // 获取当前日期
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 科目代码（默认使用UNKNOWN）
        String subjectCode = "UNK";
        if (subject != null && !subject.trim().isEmpty()) {
            // 简单的科目代码映射
            String[] subjects = {"数学", "语文", "英语", "物理", "化学", "生物", "历史", "地理", "政治"};
            String[] codes = {"MATH", "CHIN", "ENG", "PHYS", "CHEM", "BIO", "HIST", "GEO", "POLI"};
            for (int i = 0; i < subjects.length; i++) {
                if (subject.contains(subjects[i])) {
                    subjectCode = codes[i];
                    break;
                }
            }
        }

        // 查询当天已有的试卷数量，生成序号
        // int count = paperMapper.selectCount("create_time >= CURRENT_DATE");
        // String seqNo = String.format("%04d", count + 1);
        String seqNo = String.format("%04d", (int)(Math.random() * 1000));

        return subjectCode + dateStr + seqNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaper(PaperUpdateReqVO updateReqVO) {
        // 校验存在
        validatePaperExists(updateReqVO.getId());
        // 校验试卷编号唯一性
        validatePaperNoUnique(updateReqVO.getId(), updateReqVO.getPaperNo());

        // 更新数据库
        PaperDO updateObj = PaperDO.builder()
                .id(updateReqVO.getId())
                .paperNo(updateReqVO.getPaperNo())
                .studentId(updateReqVO.getStudentId())
                .subjectId(updateReqVO.getSubjectId())
                .subject(updateReqVO.getSubject())
                .title(updateReqVO.getTitle())
                .examDate(updateReqVO.getExamDate())
                .grade(updateReqVO.getGrade())
                .semester(updateReqVO.getSemester())
                .filePath(updateReqVO.getFilePath())
                .build();
        paperMapper.updateById(updateObj);

        // 处理新增的文件
        if (updateReqVO.getFiles() != null && !updateReqVO.getFiles().isEmpty()) {
            paperFileService.batchCreatePaperFiles(updateReqVO.getId(), updateReqVO.getFiles());
        }

        log.info("[updatePaper] 更新试卷成功，试卷ID: {}", updateReqVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePaper(Long id) {
        // 校验存在
        validatePaperExists(id);
        // 删除
        paperMapper.deleteById(id);
        log.info("[deletePaper] 删除试卷成功，试卷ID: {}", id);
    }

    @Override
    public PaperDO getPaper(Long id) {
        return validatePaperExists(id);
    }

    @Override
    public PageResult<PaperDO> getPaperPage(PaperPageReqVO pageReqVO) {
        return paperMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaperStatus(Long id, String status) {
        updatePaperStatus(id, status, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaperStatus(Long id, String status, String errorMsg) {
        // 校验存在
        PaperDO paper = validatePaperExists(id);

        // 更新状态
        PaperDO updateObj = PaperDO.builder()
                .id(id)
                .status(status)
                .errorMsg(errorMsg)
                .build();
        paperMapper.updateById(updateObj);

        log.info("[updatePaperStatus] 更新试卷状态成功，试卷ID: {}, 状态: {}", id, status);
    }

    @Override
    public void triggerAnalyze(Long id) {
        // 校验存在
        PaperDO paper = validatePaperExists(id);

        // 校验状态
        if (!PaperStatusEnum.READY.getCode().equals(paper.getStatus())) {
            throw exception(PAPER_STATUS_NOT_READY);
        }

        // 发布分析事件（异步处理）
        eventPublisher.publishEvent(new PaperAnalyzeEvent(id));

        log.info("[triggerAnalyze] 触发试卷分析成功，试卷ID: {}", id);
    }

    @Override
    public PaperWithQuestionsRespVO getPaperWithQuestions(Long id) {
        // 校验试卷存在
        PaperDO paper = validatePaperExists(id);

        // 查询题目列表
        List<QuestionDO> questions = questionMapper.selectListByPaperId(id);

        // 查询文件列表
        List<cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperFileDO> files =
                paperFileService.getPaperFilesByPaperId(id);

        // 构建响应VO
        PaperWithQuestionsRespVO respVO = PaperWithQuestionsRespVO.builder()
                .id(paper.getId())
                .paperNo(paper.getPaperNo())
                .studentId(paper.getStudentId())
                .subjectId(paper.getSubjectId())
                .subject(paper.getSubject())
                .title(paper.getTitle())
                .examDate(paper.getExamDate())
                .grade(paper.getGrade())
                .semester(paper.getSemester())
                .filePath(paper.getFilePath())
                .status(paper.getStatus())
                .errorMsg(paper.getErrorMsg())
                .questionCount(questions.size())
                .questions(PaperConvert.INSTANCE.convertQuestionList(questions))
                .build();

        log.info("[getPaperWithQuestions] 获取试卷详情成功，试卷ID: {}, 题目数量: {}, 文件数量: {}",
                id, questions.size(), files != null ? files.size() : 0);
        return respVO;
    }

    // ==================== 私有方法 ====================

    /**
     * 校验试卷是否存在
     */
    private PaperDO validatePaperExists(Long id) {
        PaperDO paper = paperMapper.selectById(id);
        if (paper == null) {
            throw exception(PAPER_NOT_EXISTS);
        }
        return paper;
    }

    /**
     * 校验试卷编号唯一性
     */
    private void validatePaperNoUnique(Long id, String paperNo) {
        PaperDO existing = paperMapper.selectByPaperNo(paperNo);
        if (existing == null) {
            return;
        }
        // 如果是更新，且 ID 相同，则通过
        if (id != null && existing.getId().equals(id)) {
            return;
        }
        throw exception(PAPER_NO_EXISTS);
    }

}
