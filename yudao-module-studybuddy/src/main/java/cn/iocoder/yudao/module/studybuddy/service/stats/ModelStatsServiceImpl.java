package cn.iocoder.yudao.module.studybuddy.service.stats;

import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.ModelStatsRespVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.ModelCallLogDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.stats.ModelCallLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 模型调用统计 Service 实现类
 *
 * @author StudyBuddy
 */
@Service
@Validated
@Slf4j
public class ModelStatsServiceImpl implements ModelStatsService {

    @Resource
    private ModelCallLogMapper modelCallLogMapper;

    @Override
    @Async("studybuddyTaskExecutor")
    public void recordModelCall(Long userId, String modelType, String modelName, Long paperId, Long questionId,
                                Integer inputTokens, Integer outputTokens, Integer responseTime,
                                String status, String errorMsg) {
        ModelCallLogDO logDO = ModelCallLogDO.builder()
                .userId(userId)
                .modelType(modelType)
                .modelName(modelName)
                .paperId(paperId)
                .questionId(questionId)
                .inputTokens(inputTokens)
                .outputTokens(outputTokens)
                .totalTokens((inputTokens != null ? inputTokens : 0) + (outputTokens != null ? outputTokens : 0))
                .responseTime(responseTime)
                .status(status)
                .errorMsg(errorMsg)
                .build();
        modelCallLogMapper.insert(logDO);
        log.debug("[recordModelCall] 记录模型调用: modelType={}, modelName={}, status={}", modelType, modelName, status);
    }

    @Override
    public ModelStatsRespVO getModelStats(LocalDateTime startTime, LocalDateTime endTime) {
        ModelStatsRespVO respVO = new ModelStatsRespVO();
        respVO.setModelTypeStats(modelCallLogMapper.countByModelType(startTime, endTime));
        respVO.setModelNameStats(modelCallLogMapper.countByModelName(startTime, endTime));
        respVO.setDateStats(modelCallLogMapper.countByDate(startTime, endTime));
        respVO.setSuccessRateStats(modelCallLogMapper.getSuccessRate(startTime, endTime));
        return respVO;
    }

}
