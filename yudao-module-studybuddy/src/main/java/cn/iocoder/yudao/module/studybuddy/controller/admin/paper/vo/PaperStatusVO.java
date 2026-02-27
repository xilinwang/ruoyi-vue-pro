package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 试卷状态 Response VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 试卷状态 Response VO")
@Data
public class PaperStatusVO {

    @Schema(description = "试卷ID", example = "1")
    private Long id;

    @Schema(description = "试卷编号", example = "P2025001")
    private String paperNo;

    @Schema(description = "处理状态", example = "READY")
    private String status;

    @Schema(description = "错误信息")
    private String errorMsg;

}
