package cn.iocoder.yudao.module.studybuddy.service.paper.event;

import lombok.Getter;

import java.io.Serializable;

/**
 * 答案批改事件
 *
 * @author StudyBuddy
 */
@Getter
public class AnswerGradingEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    private final Long questionId;

    /**
     * 学生答案ID
     */
    private final Long answerId;

    public AnswerGradingEvent(Long questionId, Long answerId) {
        this.questionId = questionId;
        this.answerId = answerId;
    }

}
