package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.*;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.PaperMapper;
import cn.iocoder.yudao.module.studybuddy.enums.paper.PaperStatusEnum;
import cn.iocoder.yudao.module.studybuddy.service.paper.event.PaperAnalyzeEvent;
import cn.iocoder.yudao.module.studybuddy.service.paper.event.PaperOcrEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

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
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPaper(PaperCreateReqVO createReqVO) {
        // 校验试卷编号唯一性
        validatePaperNoUnique(null, createReqVO.getPaperNo());

        // 插入数据库
        PaperDO paper = PaperDO.builder()
                .paperNo(createReqVO.getPaperNo())
                .studentId(createReqVO.getStudentId())
                .subject(createReqVO.getSubject())
                .title(createReqVO.getTitle())
                .examDate(createReqVO.getExamDate())
                .grade(createReqVO.getGrade())
                .semester(createReqVO.getSemester())
                .filePath(createReqVO.getFilePath())
                .status(PaperStatusEnum.OCR_PROCESSING.getCode())
                .build();
        paperMapper.insert(paper);

        // 发布 OCR 处理事件（异步处理）
        eventPublisher.publishEvent(new PaperOcrEvent(paper.getId(), paper.getFilePath()));

        log.info("[createPaper] 创建试卷成功，试卷ID: {}, 试卷编号: {}", paper.getId(), paper.getPaperNo());
        return paper.getId();
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
                .subject(updateReqVO.getSubject())
                .title(updateReqVO.getTitle())
                .examDate(updateReqVO.getExamDate())
                .grade(updateReqVO.getGrade())
                .semester(updateReqVO.getSemester())
                .filePath(updateReqVO.getFilePath())
                .build();
        paperMapper.updateById(updateObj);

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
