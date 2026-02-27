package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 学生答案提交 Request VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 学生答案提交 Request VO")
@Data
public class StudentAnswerSubmitReqVO {

    @Schema(description = "题目ID", required = true, example = "1")
    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    @Schema(description = "学生答案", required = true, example = "B")
    @NotBlank(message = "学生答案不能为空")
    private String rawAnswer;

    @Schema(description = "答案来源", example = "MANUAL")
    private String source;

}
