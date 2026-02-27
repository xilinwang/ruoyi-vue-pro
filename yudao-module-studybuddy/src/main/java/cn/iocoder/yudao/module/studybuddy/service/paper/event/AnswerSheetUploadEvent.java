package cn.iocoder.yudao.module.studybuddy.service.paper.event;

import lombok.Getter;

import java.io.Serializable;

/**
 * 答题卡上传处理事件
 *
 * @author StudyBuddy
 */
@Getter
public class AnswerSheetUploadEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 试卷ID
     */
    private final Long paperId;

    /**
     * 答题卡文件路径
     */
    private final String filePath;

    /**
     * 学生ID
     */
    private final Long studentId;

    public AnswerSheetUploadEvent(Long paperId, String filePath, Long studentId) {
        this.paperId = paperId;
        this.filePath = filePath;
        this.studentId = studentId;
    }

}
