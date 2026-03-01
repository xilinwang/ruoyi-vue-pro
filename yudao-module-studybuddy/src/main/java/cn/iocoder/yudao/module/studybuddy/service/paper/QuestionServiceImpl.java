package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.*;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.QuestionDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.QuestionMapper;
import cn.iocoder.yudao.module.studybuddy.enums.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 题目 Service 实现类
 *
 * @author StudyBuddy
 */
@Service
@Validated
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    @Resource
    private QuestionMapper questionMapper;

    @Override
    public QuestionDO getQuestion(Long id) {
        return questionMapper.selectById(id);
    }

    @Override
    public List<QuestionDO> getQuestionListByPaperId(Long paperId) {
        return questionMapper.selectListByPaperId(paperId);
    }

    @Override
    public void updateStandardAnswer(QuestionStandardAnswerUpdateReqVO updateReqVO) {
        log.info("[updateStandardAnswer] 更新标准答案，题目ID: {}", updateReqVO.getId());

        try {
            QuestionDO question = questionMapper.selectById(updateReqVO.getId());
            if (question == null) {
                log.error("[updateStandardAnswer] 题目不存在，题目ID: {}", updateReqVO.getId());
                throw exception(ErrorCodeConstants.QUESTION_NOT_EXISTS);
            }

            question.setStandardAnswer(updateReqVO.getStandardAnswer());
            // 更新标准答案后，重置确认状态
            question.setStandardAnswerVerified(false);

            questionMapper.updateById(question);
            log.info("[updateStandardAnswer] 标准答案更新成功，题目ID: {}", updateReqVO.getId());
        } catch (Exception e) {
            log.error("[updateStandardAnswer] 更新标准答案失败，题目ID: {}", updateReqVO.getId(), e);
            throw e;
        }
    }

    @Override
    public void verifyStandardAnswer(QuestionVerifyReqVO verifyReqVO) {
        log.info("[verifyStandardAnswer] 确认标准答案，题目ID: {}, 确认状态: {}",
                 verifyReqVO.getId(), verifyReqVO.getVerified());

        try {
            QuestionDO question = questionMapper.selectById(verifyReqVO.getId());
            if (question == null) {
                log.error("[verifyStandardAnswer] 题目不存在，题目ID: {}", verifyReqVO.getId());
                throw exception(ErrorCodeConstants.QUESTION_NOT_EXISTS);
            }

            if (verifyReqVO.getVerified() && question.getStandardAnswer() == null) {
                log.error("[verifyStandardAnswer] 标准答案为空，无法确认，题目ID: {}", verifyReqVO.getId());
                throw new IllegalArgumentException("标准答案为空，无法确认");
            }

            question.setStandardAnswerVerified(verifyReqVO.getVerified());
            questionMapper.updateById(question);
            log.info("[verifyStandardAnswer] 标准答案确认完成，题目ID: {}", verifyReqVO.getId());
        } catch (Exception e) {
            log.error("[verifyStandardAnswer] 确认标准答案失败，题目ID: {}", verifyReqVO.getId(), e);
            throw e;
        }
    }

    @Override
    public void batchVerifyStandardAnswers(Long paperId) {
        log.info("[batchVerifyStandardAnswers] 批量确认标准答案，试卷ID: {}", paperId);

        List<QuestionDO> questions = questionMapper.selectListByPaperId(paperId);
        int count = 0;
        for (QuestionDO question : questions) {
            if (question.getStandardAnswer() != null && !question.getStandardAnswerVerified()) {
                question.setStandardAnswerVerified(true);
                questionMapper.updateById(question);
                count++;
            }
        }

        log.info("[batchVerifyStandardAnswers] 批量确认完成，共确认 {} 道题目", count);
    }

    @Override
    public Long getUnverifiedCountByPaperId(Long paperId) {
        List<QuestionDO> questions = questionMapper.selectListByPaperId(paperId);
        return questions.stream()
                .filter(q -> q.getStandardAnswer() != null && !q.getStandardAnswerVerified())
                .count();
    }

    @Override
    public Long getQuestionCountByPaperId(Long paperId) {
        return (long) questionMapper.selectCountByPaperId(paperId);
    }

}
