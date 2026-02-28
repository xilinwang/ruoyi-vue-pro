package cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置 RespVO
 */
@Schema(description = "管理后台 - 系统配置 Response VO")
@Data
public class SystemConfigRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "配置键", example = "ocr.default_model")
    private String configKey;

    @Schema(description = "配置值", example = "aliyun")
    private String configValue;

    @Schema(description = "配置类型", example = "STRING")
    private String configType;

    @Schema(description = "配置描述", example = "默认OCR模型")
    private String description;

    @Schema(description = "是否公开", example = "true")
    private Boolean isPublic;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
