package cn.iocoder.yudao.module.studybuddy.service.paper.event;

import lombok.Getter;

import java.io.Serializable;

/**
 * 试卷分析处理事件
 *
 * @author StudyBuddy
 */
@Getter
public class PaperAnalyzeEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 试卷ID
     */
    private final Long paperId;

    public PaperAnalyzeEvent(Long paperId) {
        this.paperId = paperId;
    }

}
