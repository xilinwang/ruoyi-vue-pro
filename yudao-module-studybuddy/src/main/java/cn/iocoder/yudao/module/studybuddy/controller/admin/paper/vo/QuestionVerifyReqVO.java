package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 题目标准答案确认 Request VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 题目标准答案确认 Request VO")
@Data
public class QuestionVerifyReqVO {

    @Schema(description = "题目ID", required = true, example = "1")
    @NotNull(message = "题目ID不能为空")
    private Long id;

    @Schema(description = "是否确认", example = "true")
    @NotNull(message = "确认状态不能为空")
    private Boolean verified;

}
