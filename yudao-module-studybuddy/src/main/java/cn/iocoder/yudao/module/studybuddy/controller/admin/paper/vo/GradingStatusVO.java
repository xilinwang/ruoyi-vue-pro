package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 批改状态 VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 批改状态 VO")
@Data
public class GradingStatusVO {

    @Schema(description = "题目ID", example = "1")
    private Long questionId;

    @Schema(description = "批改状态", example = "GRADED")
    private String gradingStatus;

    @Schema(description = "是否正确", example = "true")
    private Boolean isCorrect;

}
