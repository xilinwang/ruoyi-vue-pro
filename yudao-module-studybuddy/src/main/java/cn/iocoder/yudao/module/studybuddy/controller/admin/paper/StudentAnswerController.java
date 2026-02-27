package cn.iocoder.yudao.module.studybuddy.controller.admin.paper;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.*;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.StudentAnswerDO;
import cn.iocoder.yudao.module.studybuddy.service.paper.StudentAnswerService;
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
 * 管理后台 - 学生答案
 *
 * @author StudyBuddy
 */
@Tag(name = "管理后台 - 学生答案")
@RestController
@RequestMapping("/studybuddy/student-answer")
@Validated
public class StudentAnswerController {

    @Resource
    private StudentAnswerService studentAnswerService;

    @PostMapping("/submit")
    @Operation(summary = "提交学生答案")
    @PreAuthorize("@ss.hasPermission('studybuddy:student-answer:submit')")
    public CommonResult<Long> submitAnswer(@Valid @RequestBody StudentAnswerSubmitReqVO submitReqVO) {
        Long answerId = studentAnswerService.submitAnswer(submitReqVO);
        return success(answerId);
    }

    @PostMapping("/batch-submit")
    @Operation(summary = "批量提交学生答案")
    @PreAuthorize("@ss.hasPermission('studybuddy:student-answer:submit')")
    public CommonResult<List<Long>> batchSubmitAnswers(@Valid @RequestBody List<StudentAnswerSubmitReqVO> submitReqVOS) {
        List<Long> answerIds = studentAnswerService.batchSubmitAnswers(submitReqVOS);
        return success(answerIds);
    }

    @PostMapping("/grade")
    @Operation(summary = "触发答案批改")
    @Parameter(name = "questionId", description = "题目ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:student-answer:grade')")
    public CommonResult<Boolean> triggerGrading(@RequestParam("questionId") Long questionId) {
        studentAnswerService.triggerGrading(questionId);
        return success(true);
    }

    @GetMapping("/get-by-question")
    @Operation(summary = "根据题目ID获取答案")
    @Parameter(name = "questionId", description = "题目ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:student-answer:query')")
    public CommonResult<StudentAnswerRespVO> getAnswerByQuestionId(@RequestParam("questionId") Long questionId) {
        StudentAnswerDO answer = studentAnswerService.getAnswerByQuestionId(questionId);
        return success(convertToRespVO(answer));
    }

    @GetMapping("/get")
    @Operation(summary = "获取答案详情")
    @Parameter(name = "id", description = "答案ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:student-answer:query')")
    public CommonResult<StudentAnswerRespVO> getAnswer(@RequestParam("id") Long id) {
        StudentAnswerDO answer = studentAnswerService.getAnswer(id);
        return success(convertToRespVO(answer));
    }

    @GetMapping("/list-by-paper")
    @Operation(summary = "根据试卷ID获取所有答案")
    @Parameter(name = "paperId", description = "试卷ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:student-answer:query')")
    public CommonResult<List<StudentAnswerRespVO>> getAnswersByPaperId(@RequestParam("paperId") Long paperId) {
        List<StudentAnswerDO> answers = studentAnswerService.getAnswersByPaperId(paperId);
        List<StudentAnswerRespVO> respVOs = answers.stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
        return success(respVOs);
    }

    @GetMapping("/grading-status")
    @Operation(summary = "获取批改状态")
    @Parameter(name = "questionId", description = "题目ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('studybuddy:student-answer:query')")
    public CommonResult<GradingStatusVO> getGradingStatus(@RequestParam("questionId") Long questionId) {
        String status = studentAnswerService.getGradingStatus(questionId);
        StudentAnswerDO answer = studentAnswerService.getAnswerByQuestionId(questionId);

        GradingStatusVO statusVO = new GradingStatusVO();
        statusVO.setQuestionId(questionId);
        statusVO.setGradingStatus(status);
        if (answer != null) {
            statusVO.setIsCorrect(answer.getIsCorrect());
        }

        return success(statusVO);
    }

    /**
     * 转换 DO 到 RespVO
     */
    private StudentAnswerRespVO convertToRespVO(StudentAnswerDO answer) {
        if (answer == null) {
            return null;
        }
        StudentAnswerRespVO respVO = new StudentAnswerRespVO();
        respVO.setId(answer.getId());
        respVO.setQuestionId(answer.getQuestionId());
        respVO.setRawAnswer(answer.getRawAnswer());
        respVO.setIsCorrect(answer.getIsCorrect());
        respVO.setErrorAnalysis(answer.getErrorAnalysis());
        respVO.setBetterSolution(answer.getBetterSolution());
        respVO.setSource(answer.getSource());
        respVO.setGradingStatus(answer.getIsCorrect() != null ? "GRADED" : "GRADING");
        respVO.setCreateTime(answer.getCreateTime());
        respVO.setUpdateTime(answer.getUpdateTime());
        return respVO;
    }

}
