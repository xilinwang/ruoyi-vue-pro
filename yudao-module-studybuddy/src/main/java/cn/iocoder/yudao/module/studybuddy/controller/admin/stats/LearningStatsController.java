package cn.iocoder.yudao.module.studybuddy.controller.admin.stats;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.LearningStatsRespVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.LearningSummaryRespVO;
import cn.iocoder.yudao.module.studybuddy.service.stats.LearningStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 学习统计 Controller
 *
 * @author StudyBuddy
 */
@Tag(name = "管理后台 - 学习统计")
@RestController
@RequestMapping("/studybuddy/stats/learning")
@Validated
public class LearningStatsController {

    @Resource
    private LearningStatsService learningStatsService;

    @GetMapping("/list")
    @Operation(summary = "获取学习统计列表")
    public CommonResult<List<LearningStatsRespVO>> getLearningStats() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(learningStatsService.getLearningStats(userId));
    }

    @GetMapping("/summary")
    @Operation(summary = "获取学习情况汇总")
    public CommonResult<LearningSummaryRespVO> getLearningSummary() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(learningStatsService.getLearningSummary(userId));
    }

    @GetMapping("/by-mastery")
    @Operation(summary = "按掌握程度获取学习统计")
    @Parameter(name = "level", description = "掌握程度(WEAK/NORMAL/GOOD/EXCELLENT)", required = true)
    public CommonResult<List<LearningStatsRespVO>> getByMasteryLevel(@RequestParam("level") String masteryLevel) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(learningStatsService.getByMasteryLevel(userId, masteryLevel));
    }

}
