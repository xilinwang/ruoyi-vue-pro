package cn.iocoder.yudao.module.studybuddy.controller.admin.subject;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.subject.vo.*;
import cn.iocoder.yudao.module.studybuddy.convert.subject.SubjectConvert;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.studybuddy.service.subject.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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
public class SubjectController {

    @Resource
    private SubjectService subjectService;

    @PostMapping("/create")
    @Operation(summary = "创建科目")
    public CommonResult<Long> createSubject(@Valid @RequestBody SubjectCreateReqVO createReqVO) {
        return success(subjectService.createSubject(createReqVO, 1L));
    }

    @PutMapping("/update")
    @Operation(summary = "更新科目")
    public CommonResult<Boolean> updateSubject(@Valid @RequestBody SubjectUpdateReqVO updateReqVO) {
        subjectService.updateSubject(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除科目")
    @Parameter(name = "id", description = "科目ID", required = true)
    public CommonResult<Boolean> deleteSubject(@RequestParam("id") Long id) {
        subjectService.deleteSubject(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得科目")
    @Parameter(name = "id", description = "科目ID", required = true, example = "1024")
    public CommonResult<SubjectRespVO> getSubject(@RequestParam("id") Long id) {
        SubjectDO subject = subjectService.getSubject(id);
        return success(SubjectConvert.INSTANCE.convert(subject));
    }

    @GetMapping("/list")
    @Operation(summary = "获取当前用户的科目列表")
    public CommonResult<List<SubjectRespVO>> getSubjectList() {
        List<SubjectDO> list = subjectService.getSubjectListByUserId(1L);
        return success(SubjectConvert.INSTANCE.convertList(list));
    }

}
