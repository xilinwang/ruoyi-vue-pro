package cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 访问统计 RespVO
 */
@Schema(description = "管理后台 - 访问统计 Response VO")
@Data
public class AccessStatsRespVO {

    @Schema(description = "总访问次数")
    private Long totalAccessCount;

    @Schema(description = "按模块统计")
    private List<Map<String, Object>> moduleStats;

    @Schema(description = "按用户统计")
    private List<Map<String, Object>> userStats;

    @Schema(description = "按日期统计")
    private List<Map<String, Object>> dateStats;

}
