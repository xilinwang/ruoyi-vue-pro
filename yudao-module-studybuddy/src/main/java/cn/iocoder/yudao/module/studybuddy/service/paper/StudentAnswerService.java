package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.*;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.StudentAnswerDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 学生答案 Service 接口
 *
 * @author StudyBuddy
 */
public interface StudentAnswerService {

    /**
     * 提交学生答案
     *
     * @param submitReqVO 提交信息
     * @return 答案ID
     */
    Long submitAnswer(@Valid StudentAnswerSubmitReqVO submitReqVO);

    /**
     * 批量提交学生答案
     *
     * @param submitReqVOS 提交信息列表
     * @return 提交成功的答案ID列表
     */
    List<Long> batchSubmitAnswers(@Valid List<StudentAnswerSubmitReqVO> submitReqVOS);

    /**
     * 触发答案批改
     *
     * @param questionId 题目ID
     */
    void triggerGrading(Long questionId);

    /**
     * 根据题目ID获取答案
     *
     * @param questionId 题目ID
     * @return 学生答案
     */
    StudentAnswerDO getAnswerByQuestionId(Long questionId);

    /**
     * 获取答案详情
     *
     * @param id 答案ID
     * @return 答案详情
     */
    StudentAnswerDO getAnswer(Long id);

    /**
     * 获取试卷的所有答案
     *
     * @param paperId 试卷ID
     * @return 答案列表
     */
    List<StudentAnswerDO> getAnswersByPaperId(Long paperId);

    /**
     * 获取批改状态
     *
     * @param questionId 题目ID
     * @return 批改状态（UNGRADED-未批改, GRADING-批改中, GRADED-已批改）
     */
    String getGradingStatus(Long questionId);

}
