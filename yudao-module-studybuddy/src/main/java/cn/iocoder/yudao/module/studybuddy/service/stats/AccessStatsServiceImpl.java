package cn.iocoder.yudao.module.studybuddy.service.stats;

import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.AccessStatsRespVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.AccessLogDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.stats.AccessLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 访问统计 Service 实现类
 *
 * @author StudyBuddy
 */
@Service
@Validated
@Slf4j
public class AccessStatsServiceImpl implements AccessStatsService {

    @Resource
    private AccessLogMapper accessLogMapper;

    @Override
    @Async("studybuddyTaskExecutor")
    public void recordAccess(Long userId, String username, String module, String operation,
                             String requestUrl, String requestMethod, Integer responseCode,
                             Integer responseTime, String ip) {
        AccessLogDO logDO = AccessLogDO.builder()
                .userId(userId)
                .username(username)
                .module(module)
                .operation(operation)
                .requestUrl(requestUrl)
                .requestMethod(requestMethod)
                .responseCode(responseCode)
                .responseTime(responseTime)
                .ip(ip)
                .build();
        accessLogMapper.insert(logDO);
        log.debug("[recordAccess] 记录访问日志: userId={}, module={}, operation={}", userId, module, operation);
    }

    @Override
    public AccessStatsRespVO getAccessStats(LocalDateTime startTime, LocalDateTime endTime) {
        AccessStatsRespVO respVO = new AccessStatsRespVO();
        respVO.setTotalAccessCount(accessLogMapper.countTotal());
        respVO.setModuleStats(accessLogMapper.countByModule(startTime, endTime));
        respVO.setUserStats(accessLogMapper.countByUser(startTime, endTime, 10));
        respVO.setDateStats(accessLogMapper.countByDate(startTime, endTime));
        return respVO;
    }

}
