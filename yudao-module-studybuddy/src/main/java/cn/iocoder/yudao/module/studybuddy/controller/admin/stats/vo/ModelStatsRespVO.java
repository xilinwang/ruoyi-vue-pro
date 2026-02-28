package cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 模型调用统计 RespVO
 */
@Schema(description = "管理后台 - 模型调用统计 Response VO")
@Data
public class ModelStatsRespVO {

    @Schema(description = "按模型类型统计")
    private List<Map<String, Object>> modelTypeStats;

    @Schema(description = "按模型名称统计")
    private List<Map<String, Object>> modelNameStats;

    @Schema(description = "按日期统计")
    private List<Map<String, Object>> dateStats;

    @Schema(description = "成功率统计")
    private List<Map<String, Object>> successRateStats;

}
