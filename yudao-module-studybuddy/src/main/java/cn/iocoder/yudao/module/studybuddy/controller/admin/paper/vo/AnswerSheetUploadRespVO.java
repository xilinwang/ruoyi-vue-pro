package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 答题卡上传 Response VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 答题卡上传 Response VO")
@Data
public class AnswerSheetUploadRespVO {

    @Schema(description = "试卷ID", example = "1")
    private Long paperId;

    @Schema(description = "学生ID", example = "1001")
    private Long studentId;

    @Schema(description = "处理状态", example = "PROCESSING")
    private String status;

    @Schema(description = "上传时间", example = "2025-01-01 12:00:00")
    private LocalDateTime uploadTime;

}
