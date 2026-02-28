package cn.iocoder.yudao.module.studybuddy.controller.admin.stats;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.ModelStatsRespVO;
import cn.iocoder.yudao.module.studybuddy.service.stats.ModelStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 模型调用统计 Controller
 *
 * @author StudyBuddy
 */
@Tag(name = "管理后台 - 模型调用统计")
@RestController
@RequestMapping("/studybuddy/stats/model")
@Validated
public class ModelStatsController {

    @Resource
    private ModelStatsService modelStatsService;

    @GetMapping("/stats")
    @Operation(summary = "获取模型调用统计")
    @PreAuthorize("@ss.hasPermission('studybuddy:stats:query')")
    public CommonResult<ModelStatsRespVO> getModelStats(
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        // 默认查询最近30天
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(30);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        return success(modelStatsService.getModelStats(startTime, endTime));
    }

}
