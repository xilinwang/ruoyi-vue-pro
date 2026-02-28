package cn.iocoder.yudao.module.studybuddy.controller.admin.stats;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookExportReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookPageReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookRespVO;
import cn.iocoder.yudao.module.studybuddy.service.stats.WrongBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 错题本 Controller
 *
 * @author StudyBuddy
 */
@Tag(name = "管理后台 - 错题本")
@RestController
@RequestMapping("/studybuddy/wrong-book")
@Validated
public class WrongBookController {

    @Resource
    private WrongBookService wrongBookService;

    @GetMapping("/page")
    @Operation(summary = "获取错题本分页")
    public CommonResult<PageResult<WrongBookRespVO>> getWrongBookPage(@Valid WrongBookPageReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(wrongBookService.getWrongBookPage(userId, reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取错题详情")
    @Parameter(name = "id", description = "错题ID", required = true)
    public CommonResult<WrongBookRespVO> getWrongBook(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(wrongBookService.getWrongBook(userId, id));
    }

    @PutMapping("/mastered")
    @Operation(summary = "更新掌握状态")
    public CommonResult<Boolean> updateMasteredStatus(
            @RequestParam("id") Long id,
            @RequestParam("mastered") Boolean isMastered) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        wrongBookService.updateMasteredStatus(userId, id, isMastered);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除错题")
    @Parameter(name = "id", description = "错题ID", required = true)
    public CommonResult<Boolean> deleteWrongBook(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        wrongBookService.deleteWrongBook(userId, id);
        return success(true);
    }

    @PostMapping("/export")
    @Operation(summary = "导出错题本")
    public void exportWrongBook(@Valid @RequestBody WrongBookExportReqVO reqVO,
                                HttpServletResponse response) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        wrongBookService.exportWrongBook(userId, reqVO, response);
    }

}
