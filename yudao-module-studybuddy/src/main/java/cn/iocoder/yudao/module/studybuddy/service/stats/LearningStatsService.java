package cn.iocoder.yudao.module.studybuddy.service.stats;

import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.LearningStatsRespVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.LearningSummaryRespVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 学习统计 Service 接口
 *
 * @author StudyBuddy
 */
public interface LearningStatsService {

    /**
     * 获取用户学习统计列表
     *
     * @param userId 用户ID
     * @return 学习统计列表
     */
    List<LearningStatsRespVO> getLearningStats(Long userId);

    /**
     * 获取学习情况汇总
     *
     * @param userId 用户ID
     * @return 学习情况汇总
     */
    LearningSummaryRespVO getLearningSummary(Long userId);

    /**
     * 更新学习统计
     *
     * @param userId          用户ID
     * @param knowledgePoint  知识点
     * @param isCorrect       是否正确
     */
    void updateLearningStats(Long userId, String knowledgePoint, boolean isCorrect);

    /**
     * 获取掌握程度为指定级别的知识点
     *
     * @param userId       用户ID
     * @param masteryLevel 掌握程度
     * @return 学习统计列表
     */
    List<LearningStatsRespVO> getByMasteryLevel(Long userId, String masteryLevel);

}
