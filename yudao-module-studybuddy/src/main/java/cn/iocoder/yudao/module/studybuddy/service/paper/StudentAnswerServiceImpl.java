package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.StudentAnswerSubmitReqVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.QuestionDO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.StudentAnswerDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.QuestionMapper;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.StudentAnswerMapper;
import cn.iocoder.yudao.module.studybuddy.service.paper.event.AnswerGradingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生答案 Service 实现类
 *
 * @author StudyBuddy
 */
@Service
@Validated
@Slf4j
public class StudentAnswerServiceImpl implements StudentAnswerService {

    @Resource
    private StudentAnswerMapper studentAnswerMapper;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitAnswer(StudentAnswerSubmitReqVO submitReqVO) {
        log.info("[submitAnswer] 提交学生答案，题目ID: {}", submitReqVO.getQuestionId());

        // 检查题目是否存在
        QuestionDO question = questionMapper.selectById(submitReqVO.getQuestionId());
        if (question == null) {
            throw new IllegalArgumentException("题目不存在，ID: " + submitReqVO.getQuestionId());
        }

        // 检查是否已存在答案
        StudentAnswerDO existingAnswer = studentAnswerMapper.selectByQuestionId(submitReqVO.getQuestionId());
        if (existingAnswer != null) {
            // 更新现有答案
            existingAnswer.setRawAnswer(submitReqVO.getRawAnswer());
            existingAnswer.setSource(submitReqVO.getSource() != null ? submitReqVO.getSource() : "MANUAL");
            existingAnswer.setIsCorrect(null);
            existingAnswer.setErrorAnalysis(null);
            existingAnswer.setBetterSolution(null);
            studentAnswerMapper.updateById(existingAnswer);

            // 触发批改
            triggerGrading(submitReqVO.getQuestionId());

            return existingAnswer.getId();
        }

        // 创建新答案
        StudentAnswerDO studentAnswer = StudentAnswerDO.builder()
                .questionId(submitReqVO.getQuestionId())
                .rawAnswer(submitReqVO.getRawAnswer())
                .source(submitReqVO.getSource() != null ? submitReqVO.getSource() : "MANUAL")
                .build();

        studentAnswerMapper.insert(studentAnswer);

        // 触发批改
        triggerGrading(submitReqVO.getQuestionId());

        log.info("[submitAnswer] 学生答案提交完成，答案ID: {}", studentAnswer.getId());
        return studentAnswer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchSubmitAnswers(List<StudentAnswerSubmitReqVO> submitReqVOS) {
        log.info("[batchSubmitAnswers] 批量提交学生答案，数量: {}", submitReqVOS.size());

        List<Long> answerIds = new ArrayList<>();
        for (StudentAnswerSubmitReqVO reqVO : submitReqVOS) {
            try {
                Long answerId = submitAnswer(reqVO);
                answerIds.add(answerId);
            } catch (Exception e) {
                log.error("[batchSubmitAnswers] 提交答案失败，题目ID: {}", reqVO.getQuestionId(), e);
            }
        }

        log.info("[batchSubmitAnswers] 批量提交完成，成功: {}/{}", answerIds.size(), submitReqVOS.size());
        return answerIds;
    }

    @Override
    public void triggerGrading(Long questionId) {
        log.info("[triggerGrading] 触发答案批改，题目ID: {}", questionId);

        // 检查题目是否有标准答案
        QuestionDO question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("题目不存在，ID: " + questionId);
        }

        if (question.getStandardAnswer() == null || !question.getStandardAnswerVerified()) {
            log.warn("[triggerGrading] 题目 {} 没有已确认的标准答案，跳过批改", questionId);
            return;
        }

        // 检查是否有学生答案
        StudentAnswerDO studentAnswer = studentAnswerMapper.selectByQuestionId(questionId);
        if (studentAnswer == null) {
            log.warn("[triggerGrading] 题目 {} 没有学生答案，跳过批改", questionId);
            return;
        }

        // 发布批改事件
        AnswerGradingEvent event = new AnswerGradingEvent(questionId, studentAnswer.getId());
        applicationContext.publishEvent(event);

        log.info("[triggerGrading] 批改事件已发布，题目ID: {}", questionId);
    }

    @Override
    public StudentAnswerDO getAnswerByQuestionId(Long questionId) {
        return studentAnswerMapper.selectByQuestionId(questionId);
    }

    @Override
    public StudentAnswerDO getAnswer(Long id) {
        return studentAnswerMapper.selectById(id);
    }

    @Override
    public List<StudentAnswerDO> getAnswersByPaperId(Long paperId) {
        // 获取试卷的所有题目
        List<QuestionDO> questions = questionMapper.selectListByPaperId(paperId);

        // 获取所有题目的答案
        List<StudentAnswerDO> answers = new ArrayList<>();
        for (QuestionDO question : questions) {
            StudentAnswerDO answer = studentAnswerMapper.selectByQuestionId(question.getId());
            if (answer != null) {
                answers.add(answer);
            }
        }

        return answers;
    }

    @Override
    public String getGradingStatus(Long questionId) {
        StudentAnswerDO answer = studentAnswerMapper.selectByQuestionId(questionId);

        if (answer == null) {
            return "UNGRADED";
        }

        if (answer.getIsCorrect() != null) {
            return "GRADED";
        }

        return "GRADING";
    }

}
