package cn.iocoder.yudao.module.studybuddy.service.stats;

import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.AccessStatsRespVO;

import java.time.LocalDateTime;

/**
 * 访问统计 Service 接口
 *
 * @author StudyBuddy
 */
public interface AccessStatsService {

    /**
     * 记录访问日志
     *
     * @param userId       用户ID
     * @param username     用户名
     * @param module       模块
     * @param operation    操作
     * @param requestUrl   请求URL
     * @param requestMethod 请求方法
     * @param responseCode 响应码
     * @param responseTime 响应时间
     * @param ip           IP地址
     */
    void recordAccess(Long userId, String username, String module, String operation,
                      String requestUrl, String requestMethod, Integer responseCode,
                      Integer responseTime, String ip);

    /**
     * 获取访问统计
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 访问统计
     */
    AccessStatsRespVO getAccessStats(LocalDateTime startTime, LocalDateTime endTime);

}
