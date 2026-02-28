package cn.iocoder.yudao.module.studybuddy.controller.admin.subject.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 科目创建 Request VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 科目创建 Request VO")
@Data
public class SubjectCreateReqVO {

    @Schema(description = "科目名称", required = true, example = "数学")
    @NotBlank(message = "科目名称不能为空")
    private String name;

    @Schema(description = "科目描述", example = "高中数学课程")
    private String description;

}
