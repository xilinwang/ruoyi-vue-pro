package cn.iocoder.yudao.module.studybuddy.controller.admin.paper;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.*;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.QuestionDO;
import cn.iocoder.yudao.module.studybuddy.service.paper.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 题目
 *
 * @author StudyBuddy
 */
@Tag(name = "管理后台 - 题目")
@RestController
@RequestMapping("/studybuddy/question")
@Validated
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @GetMapping("/get")
    @Operation(summary = "获取题目详情")
    @Parameter(name = "id", description = "题目ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:question:query')")
    public CommonResult<QuestionRespVO> getQuestion(@RequestParam("id") Long id) {
        QuestionDO question = questionService.getQuestion(id);
        QuestionRespVO respVO = convertToRespVO(question);
        return success(respVO);
    }

    @GetMapping("/list-by-paper")
    @Operation(summary = "根据试卷ID获取题目列表")
    @Parameter(name = "paperId", description = "试卷ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:question:query')")
    public CommonResult<List<QuestionRespVO>> getQuestionListByPaperId(@RequestParam("paperId") Long paperId) {
        List<QuestionDO> questions = questionService.getQuestionListByPaperId(paperId);
        List<QuestionRespVO> respVOs = questions.stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
        return success(respVOs);
    }

    @PutMapping("/update-standard-answer")
    @Operation(summary = "更新标准答案")
    @PreAuthorize("@ss.hasPermission('studybuddy:question:update')")
    public CommonResult<Boolean> updateStandardAnswer(@Valid @RequestBody QuestionStandardAnswerUpdateReqVO updateReqVO) {
        questionService.updateStandardAnswer(updateReqVO);
        return success(true);
    }

    @PostMapping("/verify-standard-answer")
    @Operation(summary = "确认标准答案")
    @PreAuthorize("@ss.hasPermission('studybuddy:question:update')")
    public CommonResult<Boolean> verifyStandardAnswer(@Valid @RequestBody QuestionVerifyReqVO verifyReqVO) {
        questionService.verifyStandardAnswer(verifyReqVO);
        return success(true);
    }

    @PostMapping("/batch-verify-standard-answers")
    @Operation(summary = "批量确认标准答案")
    @Parameter(name = "paperId", description = "试卷ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:question:update')")
    public CommonResult<Boolean> batchVerifyStandardAnswers(@RequestParam("paperId") Long paperId) {
        questionService.batchVerifyStandardAnswers(paperId);
        return success(true);
    }

    @GetMapping("/unverified-count")
    @Operation(summary = "获取未确认标准答案的题目数量")
    @Parameter(name = "paperId", description = "试卷ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:question:query')")
    public CommonResult<Long> getUnverifiedCount(@RequestParam("paperId") Long paperId) {
        Long count = questionService.getUnverifiedCountByPaperId(paperId);
        return success(count);
    }

    /**
     * 转换 DO 到 RespVO
     */
    private QuestionRespVO convertToRespVO(QuestionDO question) {
        if (question == null) {
            return null;
        }
        QuestionRespVO respVO = new QuestionRespVO();
        respVO.setId(question.getId());
        respVO.setPaperId(question.getPaperId());
        respVO.setQuestionNo(question.getQuestionNo());
        respVO.setContent(question.getContent());
        respVO.setKnowledgePoint(question.getKnowledgePoint());
        respVO.setPositionJson(question.getPositionJson());
        respVO.setStandardAnswer(question.getStandardAnswer());
        respVO.setStandardAnswerVerified(question.getStandardAnswerVerified());
        respVO.setCreateTime(question.getCreateTime());
        respVO.setUpdateTime(question.getUpdateTime());
        return respVO;
    }

}
