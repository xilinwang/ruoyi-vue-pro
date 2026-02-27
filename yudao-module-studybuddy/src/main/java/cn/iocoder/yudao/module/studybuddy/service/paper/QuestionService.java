package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.*;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.QuestionDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 题目 Service 接口
 *
 * @author StudyBuddy
 */
public interface QuestionService {

    /**
     * 获取题目详情
     *
     * @param id 题目ID
     * @return 题目详情
     */
    QuestionDO getQuestion(Long id);

    /**
     * 根据试卷ID获取题目列表
     *
     * @param paperId 试卷ID
     * @return 题目列表
     */
    List<QuestionDO> getQuestionListByPaperId(Long paperId);

    /**
     * 更新标准答案
     *
     * @param updateReqVO 更新信息
     */
    void updateStandardAnswer(@Valid QuestionStandardAnswerUpdateReqVO updateReqVO);

    /**
     * 确认标准答案
     *
     * @param verifyReqVO 确认信息
     */
    void verifyStandardAnswer(@Valid QuestionVerifyReqVO verifyReqVO);

    /**
     * 批量确认标准答案
     *
     * @param paperId 试卷ID
     */
    void batchVerifyStandardAnswers(Long paperId);

    /**
     * 获取未确认标准答案的题目数量
     *
     * @param paperId 试卷ID
     * @return 未确认数量
     */
    Long getUnverifiedCountByPaperId(Long paperId);

}
