package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.*;
import cn.iocoder.yudao.module.studybuddy.convert.paper.PaperConvert;
import cn.iocoder.yudao.module.studybuddy.convert.paper.PaperFileConvert;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperDO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.QuestionDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.PaperMapper;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.QuestionMapper;
import cn.iocoder.yudao.module.studybuddy.enums.paper.PaperStatusEnum;
import cn.iocoder.yudao.module.studybuddy.service.paper.event.PaperAnalyzeEvent;
import cn.iocoder.yudao.module.studybuddy.service.paper.event.PaperOcrEvent;
import cn.iocoder.yudao.module.studybuddy.service.subject.SubjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    private SubjectService subjectService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPaper(PaperCreateReqVO createReqVO) {
        // 自动生成试卷编号（如果未提供）
        String paperNo = createReqVO.getPaperNo();
        if (paperNo == null || paperNo.trim().isEmpty()) {
            paperNo = generatePaperNo(createReqVO.getSubjectId());
        } else {
            // 校验试卷编号唯一性
            validatePaperNoUnique(null, paperNo);
        }

        // 默认学生ID（如果未提供，默认为1即管理员）
        Long studentId = createReqVO.getStudentId();
        if (studentId == null) {
            studentId = 1L;
        }

        // 解析科目名称（如果提供了科目ID但未提供科目名称）
        String subjectName = createReqVO.getSubject();
        if (createReqVO.getSubjectId() != null && (subjectName == null || subjectName.trim().isEmpty())) {
            subjectName = subjectService.getSubject(createReqVO.getSubjectId()).getName();
        }

        // 插入数据库
        PaperDO paper = PaperDO.builder()
                .paperNo(paperNo)
                .studentId(studentId)
                .subjectId(createReqVO.getSubjectId())
                .subject(subjectName)
                .title(createReqVO.getTitle())
                .description(createReqVO.getDescription())
                .examDate(createReqVO.getExamDate())
                .grade(createReqVO.getGrade())
                .semester(createReqVO.getSemester())
                .filePath(createReqVO.getFilePath())
                .status(PaperStatusEnum.UPLOADED.getCode())
                .ocrModel(createReqVO.getOcrModel())
                .build();
        paperMapper.insert(paper);

        // 处理多文件上传（不自动触发OCR）
        if (createReqVO.getFiles() != null && !createReqVO.getFiles().isEmpty()) {
            paperFileService.batchCreatePaperFiles(paper.getId(), createReqVO.getFiles());
        }

        log.info("[createPaper] 创建试卷成功，试卷ID: {}, 试卷编号: {}", paper.getId(), paper.getPaperNo());
        return paper.getId();
    }

    /**
     * 生成试卷编号
     * 规则: 科目代码 + 8位数字序号（从00000001开始）
     * 例如: MATH00000001, CHIN00000002
     * 每个科目代号独立编号，从1开始递增
     * 注意：只匹配11位长度的编号（科目代码3位 + 8位数字）
     */
    private String generatePaperNo(Long subjectId) {
        // 获取科目代号
        String subjectCode = "SUB";
        if (subjectId != null) {
            try {
                cn.iocoder.yudao.module.studybuddy.dal.dataobject.subject.SubjectDO subject =
                        subjectService.getSubject(subjectId);
                if (subject != null && subject.getCode() != null && !subject.getCode().isEmpty()) {
                    subjectCode = subject.getCode().toUpperCase();
                }
            } catch (Exception e) {
                // 如果获取失败，使用默认代号
            }
        }

        // 查询该科目代号下符合新格式（科目代码+8位数字）的试卷数量
        Long count = paperMapper.selectCountByNewFormat(subjectCode, subjectCode.length());

        String seqNo = String.format("%08d", count + 1);
        return subjectCode + seqNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaper(PaperUpdateReqVO updateReqVO) {
        // 校验存在
        PaperDO existingPaper = validatePaperExists(updateReqVO.getId());
        // 校验试卷编号唯一性（仅在传了 paperNo 时校验）
        validatePaperNoUnique(updateReqVO.getId(), updateReqVO.getPaperNo());

        // 解析科目名称（如果提供了科目ID）
        String subjectName = updateReqVO.getSubject();
        if (updateReqVO.getSubjectId() != null && subjectName == null) {
            subjectName = subjectService.getSubject(updateReqVO.getSubjectId()).getName();
        }

        // 更新数据库（如果 paperNo 为空，保留原有的 paperNo）
        PaperDO updateObj = PaperDO.builder()
                .id(updateReqVO.getId())
                .paperNo(updateReqVO.getPaperNo() != null ? updateReqVO.getPaperNo() : existingPaper.getPaperNo())
                .studentId(updateReqVO.getStudentId())
                .subjectId(updateReqVO.getSubjectId())
                .subject(subjectName)
                .title(updateReqVO.getTitle())
                .description(updateReqVO.getDescription())
                .examDate(updateReqVO.getExamDate())
                .grade(updateReqVO.getGrade())
                .semester(updateReqVO.getSemester())
                .filePath(updateReqVO.getFilePath())
                .build();
        paperMapper.updateById(updateObj);

        // 处理文件更新（如果提供了文件列表）
        log.info("[updatePaper] files 参数: {}, 是否为 null: {}", 
                updateReqVO.getFiles() != null ? "size=" + updateReqVO.getFiles().size() : "null",
                updateReqVO.getFiles() == null);
        if (updateReqVO.getFiles() != null) {
            // 获取当前试卷的所有文件
            List<cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperFileDO> currentFiles =
                    paperFileService.getPaperFilesByPaperId(updateReqVO.getId());
            log.info("[updatePaper] 当前文件数量: {}", currentFiles.size());

            // 简化策略：删除所有现有文件，再添加新文件列表
            // 这样可以正确处理重复路径的文件数量变化
            for (cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperFileDO currentFile : currentFiles) {
                log.info("[updatePaper] 删除试卷文件，文件ID: {}, 路径: {}", currentFile.getId(), currentFile.getFilePath());
                paperFileService.deletePaperFile(currentFile.getId());
            }

            // 添加新文件列表
            if (!updateReqVO.getFiles().isEmpty()) {
                log.info("[updatePaper] 添加新试卷文件，数量: {}", updateReqVO.getFiles().size());
                paperFileService.batchCreatePaperFiles(updateReqVO.getId(), updateReqVO.getFiles());
            }
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
    public void triggerOcr(Long id) {
        // 校验存在
        PaperDO paper = validatePaperExists(id);

        // 校验状态：只有已上传状态才能触发OCR
        if (!PaperStatusEnum.UPLOADED.getCode().equals(paper.getStatus())) {
            throw exception(PAPER_STATUS_NOT_UPLOADED);
        }

        // 获取试卷文件
        List<cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperFileDO> files =
                paperFileService.getPaperFilesByPaperId(id);
        
        if (files.isEmpty() && (paper.getFilePath() == null || paper.getFilePath().trim().isEmpty())) {
            throw exception(PAPER_FILE_NOT_EXISTS);
        }

        // 更新状态为OCR处理中
        updatePaperStatus(id, PaperStatusEnum.OCR_PROCESSING.getCode());

        // 发布OCR事件 - 使用阿里云读光 OCR
        String filePath = !files.isEmpty() ? files.get(0).getFilePath() : paper.getFilePath();
        String ocrModel = "aliyun";
        // 获取试卷科目，传递给OCR服务
        String subject = paper.getSubject();
        eventPublisher.publishEvent(new PaperOcrEvent(id, filePath, ocrModel, subject));

        log.info("[triggerOcr] 触发OCR识别成功，试卷ID: {}, 文件路径: {}, OCR模型: {}, 科目: {}",
                id, filePath, ocrModel, subject);
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
                .files(PaperFileConvert.INSTANCE.convertList(files))
                .build();

        log.info("[getPaperWithQuestions] 获取试卷详情成功，试卷ID: {}, 题目数量: {}, 文件数量: {}",
                id, questions.size(), files.size());
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
        // 如果 paperNo 为空，跳过校验（编辑时可能不传 paperNo）
        if (paperNo == null || paperNo.trim().isEmpty()) {
            return;
        }
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
