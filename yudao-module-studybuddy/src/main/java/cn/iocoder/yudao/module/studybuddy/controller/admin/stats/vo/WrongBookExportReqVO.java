package cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 错题本导出 ReqVO
 */
@Schema(description = "管理后台 - 错题本导出 Request VO")
@Data
public class WrongBookExportReqVO {

    @Schema(description = "错题ID列表（为空则导出全部）", example = "[1, 2, 3]")
    private List<Long> ids;

    @Schema(description = "导出格式", requiredMode = Schema.RequiredMode.REQUIRED, example = "docx")
    private String format;

    @Schema(description = "是否只导出未掌握的错题", example = "true")
    private Boolean onlyNotMastered;

}
