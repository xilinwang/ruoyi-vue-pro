package cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 系统配置更新 ReqVO
 */
@Schema(description = "管理后台 - 系统配置更新 Request VO")
@Data
public class SystemConfigUpdateReqVO {

    @Schema(description = "配置键", requiredMode = Schema.RequiredMode.REQUIRED, example = "ocr.default_model")
    @NotBlank(message = "配置键不能为空")
    private String configKey;

    @Schema(description = "配置值", requiredMode = Schema.RequiredMode.REQUIRED, example = "aliyun")
    @NotBlank(message = "配置值不能为空")
    private String configValue;

}
