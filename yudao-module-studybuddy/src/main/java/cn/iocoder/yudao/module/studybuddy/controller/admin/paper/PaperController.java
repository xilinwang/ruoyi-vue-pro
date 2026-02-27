package cn.iocoder.yudao.module.studybuddy.controller.admin.paper;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.*;
import cn.iocoder.yudao.module.studybuddy.convert.paper.PaperConvert;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperDO;
import cn.iocoder.yudao.module.studybuddy.service.paper.PaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 试卷
 *
 * @author StudyBuddy
 */
@Tag(name = "管理后台 - 试卷")
@RestController
@RequestMapping("/studybuddy/paper")
@Validated
public class PaperController {

    @Resource
    private PaperService paperService;

    @PostMapping("/create")
    @Operation(summary = "创建试卷")
    @PreAuthorize("@ss.hasPermission('studybuddy:paper:create')")
    public CommonResult<Long> createPaper(@Valid @RequestBody PaperCreateReqVO reqVO) {
        return success(paperService.createPaper(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新试卷")
    @PreAuthorize("@ss.hasPermission('studybuddy:paper:update')")
    public CommonResult<Boolean> updatePaper(@Valid @RequestBody PaperUpdateReqVO reqVO) {
        paperService.updatePaper(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除试卷")
    @Parameter(name = "id", description = "试卷ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:paper:delete')")
    public CommonResult<Boolean> deletePaper(@RequestParam("id") Long id) {
        paperService.deletePaper(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取试卷详情")
    @Parameter(name = "id", description = "试卷ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:paper:query')")
    public CommonResult<PaperRespVO> getPaper(@RequestParam("id") Long id) {
        PaperDO paper = paperService.getPaper(id);
        return success(PaperConvert.INSTANCE.convert(paper));
    }

    @GetMapping("/get-with-questions")
    @Operation(summary = "获取试卷详情（含题目）")
    @Parameter(name = "id", description = "试卷ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:paper:query')")
    public CommonResult<PaperWithQuestionsRespVO> getPaperWithQuestions(@RequestParam("id") Long id) {
        return success(paperService.getPaperWithQuestions(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获取试卷分页")
    @PreAuthorize("@ss.hasPermission('studybuddy:paper:query')")
    public CommonResult<PageResult<PaperRespVO>> getPaperPage(@Valid PaperPageReqVO pageReqVO) {
        PageResult<PaperDO> pageResult = paperService.getPaperPage(pageReqVO);
        return success(PaperConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/status")
    @Operation(summary = "查询试卷状态")
    @Parameter(name = "id", description = "试卷ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:paper:query')")
    public CommonResult<PaperStatusVO> getPaperStatus(@RequestParam("id") Long id) {
        PaperDO paper = paperService.getPaper(id);
        PaperStatusVO statusVO = new PaperStatusVO();
        statusVO.setId(paper.getId());
        statusVO.setPaperNo(paper.getPaperNo());
        statusVO.setStatus(paper.getStatus());
        statusVO.setErrorMsg(paper.getErrorMsg());
        return success(statusVO);
    }

    @PostMapping("/analyze")
    @Operation(summary = "触发试卷分析")
    @Parameter(name = "id", description = "试卷ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:paper:analyze')")
    public CommonResult<Boolean> triggerAnalyze(@RequestParam("id") Long id) {
        paperService.triggerAnalyze(id);
        return success(true);
    }

}
