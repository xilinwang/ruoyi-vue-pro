package cn.iocoder.yudao.module.studybuddy.controller.admin.subject;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.studybuddy.controller.admin.subject.vo.*;
import cn.iocoder.yudao.module.studybuddy.convert.subject.SubjectConvert;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.studybuddy.service.subject.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 科目管理 Controller
 *
 * @author StudyBuddy
 */
@Tag(name = "管理后台 - 科目管理")
@RestController
@RequestMapping("/studybuddy/subject")
@Validated
public class SubjectController {

    @Resource
    private SubjectService subjectService;

    @PostMapping("/create")
    @Operation(summary = "创建科目")
    @PreAuthorize("@ss.hasPermission('studybuddy:subject:create')")
    public CommonResult<Long> createSubject(@Valid @RequestBody SubjectCreateReqVO createReqVO) {
        return success(subjectService.createSubject(createReqVO, getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新科目")
    @PreAuthorize("@ss.hasPermission('studybuddy:subject:update')")
    public CommonResult<Boolean> updateSubject(@Valid @RequestBody SubjectUpdateReqVO updateReqVO) {
        subjectService.updateSubject(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除科目")
    @Parameter(name = "id", description = "科目ID", required = true)
    @PreAuthorize("@ss.hasPermission('studybuddy:subject:delete')")
    public CommonResult<Boolean> deleteSubject(@RequestParam("id") Long id) {
        subjectService.deleteSubject(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得科目")
    @Parameter(name = "id", description = "科目ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('studybuddy:subject:query')")
    public CommonResult<SubjectRespVO> getSubject(@RequestParam("id") Long id) {
        SubjectDO subject = subjectService.getSubject(id);
        return success(SubjectConvert.INSTANCE.convert(subject));
    }

    @GetMapping("/page")
    @Operation(summary = "获得科目分页")
    @PreAuthorize("@ss.hasPermission('studybuddy:subject:query')")
    public CommonResult<PageResult<SubjectRespVO>> getSubjectPage(@Valid SubjectPageReqVO pageReqVO) {
        PageResult<SubjectDO> pageResult = subjectService.getSubjectPage(pageReqVO);
        return success(SubjectConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list")
    @Operation(summary = "获取当前用户的科目列表")
    @PreAuthorize("@ss.hasPermission('studybuddy:subject:query')")
    public CommonResult<List<SubjectRespVO>> getSubjectList() {
        List<SubjectDO> list = subjectService.getSubjectListByUserId(getLoginUserId());
        return success(SubjectConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出科目 Excel")
    @PreAuthorize("@ss.hasPermission('studybuddy:subject:export')")
    public void exportSubjectExcel(@Valid SubjectPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(Integer.MAX_VALUE);
        List<SubjectDO> list = subjectService.getSubjectPage(pageReqVO).getList();
        ExcelUtils.write(response, "科目.xls", "数据", SubjectRespVO.class,
                SubjectConvert.INSTANCE.convertList(list));
    }

    private Long getLoginUserId() {
        // TODO: 从安全上下文获取登录用户ID
        return 1L; // 临时返回固定值
    }

}
