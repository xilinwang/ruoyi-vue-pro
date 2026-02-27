package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生答案 Response VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 学生答案 Response VO")
@Data
public class StudentAnswerRespVO {

    @Schema(description = "答案ID", example = "1")
    private Long id;

    @Schema(description = "题目ID", example = "1")
    private Long questionId;

    @Schema(description = "学生原始答案", example = "B")
    private String rawAnswer;

    @Schema(description = "是否正确", example = "true")
    private Boolean isCorrect;

    @Schema(description = "错误分析")
    private String errorAnalysis;

    @Schema(description = "更优解法")
    private String betterSolution;

    @Schema(description = "答案来源", example = "MANUAL")
    private String source;

    @Schema(description = "批改状态", example = "GRADED")
    private String gradingStatus;

    @Schema(description = "创建时间", example = "2025-01-01 12:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-01-01 12:00:00")
    private LocalDateTime updateTime;

}
