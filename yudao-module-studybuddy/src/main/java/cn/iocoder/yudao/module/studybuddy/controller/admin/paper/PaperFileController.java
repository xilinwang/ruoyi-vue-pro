package cn.iocoder.yudao.module.studybuddy.controller.admin.paper;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.PaperCreateReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.PaperFileRespVO;
import cn.iocoder.yudao.module.studybuddy.convert.paper.PaperFileConvert;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperFileDO;
import cn.iocoder.yudao.module.studybuddy.service.paper.PaperFileService;
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
 * 试卷文件管理 Controller
 *
 * @author StudyBuddy
 */
@Tag(name = "管理后台 - 试卷文件管理")
@RestController
@RequestMapping("/studybuddy/paper-file")
@Validated
public class PaperFileController {

    @Resource
    private PaperFileService paperFileService;

    @PostMapping("/create")
    @Operation(summary = "创建试卷文件")
    @PreAuthorize("@ss.hasPermission('studybuddy:paper-file:create')")
    public CommonResult<Long> createPaperFile(
            @Parameter(description = "试卷ID", required = true) @RequestParam("paperId") Long paperId,
            @Valid @RequestBody PaperCreateReqVO.FileCreateInfo fileInfo) {
        return success(paperFileService.createPaperFile(paperId, fileInfo));
    }

    @PostMapping("/batch-create")
    @Operation(summary = "批量创建试卷文件")
    @PreAuthorize("@ss.hasPermission('studybuddy:paper-file:create')")
    public CommonResult<List<Long>> batchCreatePaperFiles(
            @Parameter(description = "试卷ID", required = true) @RequestParam("paperId") Long paperId,
            @Valid @RequestBody List<PaperCreateReqVO.FileCreateInfo> fileInfos) {
        return success(paperFileService.batchCreatePaperFiles(paperId, fileInfos));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除试卷文件")
    @Parameter(name = "id", description = "文件ID", required = true)
    @PreAuthorize("@ss.hasPermission('studybuddy:paper-file:delete')")
    public CommonResult<Boolean> deletePaperFile(@RequestParam("id") Long id) {
        paperFileService.deletePaperFile(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获得试卷的文件列表")
    @Parameter(name = "paperId", description = "试卷ID", required = true)
    @PreAuthorize("@ss.hasPermission('studybuddy:paper-file:query')")
    public CommonResult<List<PaperFileRespVO>> getPaperFileList(@RequestParam("paperId") Long paperId) {
        List<PaperFileDO> list = paperFileService.getPaperFilesByPaperId(paperId);
        return success(PaperFileConvert.INSTANCE.convertList(list));
    }

}
