package cn.iocoder.yudao.module.studybuddy.controller.admin.stats;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.SystemConfigRespVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.SystemConfigUpdateReqVO;
import cn.iocoder.yudao.module.studybuddy.service.stats.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 系统配置 Controller
 *
 * @author StudyBuddy
 */
@Tag(name = "管理后台 - 系统配置")
@RestController
@RequestMapping("/studybuddy/stats/config")
@Validated
public class SystemConfigController {

    @Resource
    private SystemConfigService systemConfigService;

    @GetMapping("/list")
    @Operation(summary = "获取所有系统配置")
    @PreAuthorize("@ss.hasPermission('studybuddy:config:query')")
    public CommonResult<List<SystemConfigRespVO>> getAllConfigs() {
        return success(systemConfigService.getAllConfigs());
    }

    @GetMapping("/public")
    @Operation(summary = "获取公开的系统配置")
    public CommonResult<List<SystemConfigRespVO>> getPublicConfigs() {
        return success(systemConfigService.getPublicConfigs());
    }

    @GetMapping("/get")
    @Operation(summary = "获取配置值")
    @Parameter(name = "key", description = "配置键", required = true)
    @PreAuthorize("@ss.hasPermission('studybuddy:config:query')")
    public CommonResult<String> getConfigValue(@RequestParam("key") String configKey) {
        return success(systemConfigService.getConfigValue(configKey));
    }

    @PutMapping("/update")
    @Operation(summary = "更新系统配置")
    @PreAuthorize("@ss.hasPermission('studybuddy:config:update')")
    public CommonResult<Boolean> updateConfig(@Valid @RequestBody SystemConfigUpdateReqVO reqVO) {
        systemConfigService.updateConfig(reqVO);
        return success(true);
    }

}
