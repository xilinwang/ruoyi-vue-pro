package cn.iocoder.yudao.module.studybuddy.controller.admin.paper;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.AnswerSheetUploadReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.AnswerSheetUploadRespVO;
import cn.iocoder.yudao.module.studybuddy.service.paper.AnswerSheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 答题卡
 *
 * @author StudyBuddy
 */
@Tag(name = "管理后台 - 答题卡")
@RestController
@RequestMapping("/studybuddy/answer-sheet")
@Validated
public class AnswerSheetController {

    @Resource
    private AnswerSheetService answerSheetService;

    @PostMapping("/upload")
    @Operation(summary = "上传答题卡")
    @PreAuthorize("@ss.hasPermission('studybuddy:answer-sheet:upload')")
    public CommonResult<AnswerSheetUploadRespVO> uploadAnswerSheet(@Valid @RequestBody AnswerSheetUploadReqVO reqVO) {
        String status = answerSheetService.uploadAnswerSheet(reqVO);

        AnswerSheetUploadRespVO respVO = new AnswerSheetUploadRespVO();
        respVO.setPaperId(reqVO.getPaperId());
        respVO.setStudentId(reqVO.getStudentId());
        respVO.setStatus(status);
        respVO.setUploadTime(LocalDateTime.now());

        return success(respVO);
    }

}
