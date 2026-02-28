package cn.iocoder.yudao.module.studybuddy.service.stats;

import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.ModelStatsRespVO;

import java.time.LocalDateTime;

/**
 * 模型调用统计 Service 接口
 *
 * @author StudyBuddy
 */
public interface ModelStatsService {

    /**
     * 记录模型调用
     *
     * @param userId       用户ID
     * @param modelType    模型类型
     * @param modelName    模型名称
     * @param paperId      试卷ID
     * @param questionId   题目ID
     * @param inputTokens  输入Token
     * @param outputTokens 输出Token
     * @param responseTime 响应时间
     * @param status       状态
     * @param errorMsg     错误信息
     */
    void recordModelCall(Long userId, String modelType, String modelName, Long paperId, Long questionId,
                         Integer inputTokens, Integer outputTokens, Integer responseTime,
                         String status, String errorMsg);

    /**
     * 获取模型调用统计
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 模型调用统计
     */
    ModelStatsRespVO getModelStats(LocalDateTime startTime, LocalDateTime endTime);

}
