package cn.iocoder.yudao.module.studybuddy.service.stats;

import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.SystemConfigRespVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.SystemConfigUpdateReqVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.SystemConfigDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.stats.SystemConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置 Service 实现类
 *
 * @author StudyBuddy
 */
@Service
@Validated
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

    @Resource
    private SystemConfigMapper systemConfigMapper;

    @Override
    public String getConfigValue(String configKey) {
        SystemConfigDO config = systemConfigMapper.selectByConfigKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public List<SystemConfigRespVO> getPublicConfigs() {
        List<SystemConfigDO> configs = systemConfigMapper.selectList(SystemConfigDO::getIsPublic, true);
        return configs.stream().map(this::convertToRespVO).collect(Collectors.toList());
    }

    @Override
    public List<SystemConfigRespVO> getAllConfigs() {
        List<SystemConfigDO> configs = systemConfigMapper.selectList();
        return configs.stream().map(this::convertToRespVO).collect(Collectors.toList());
    }

    @Override
    public void updateConfig(SystemConfigUpdateReqVO reqVO) {
        SystemConfigDO config = systemConfigMapper.selectByConfigKey(reqVO.getConfigKey());
        if (config == null) {
            // 创建新配置
            config = SystemConfigDO.builder()
                    .configKey(reqVO.getConfigKey())
                    .configValue(reqVO.getConfigValue())
                    .configType("STRING")
                    .isPublic(false)
                    .build();
            systemConfigMapper.insert(config);
        } else {
            // 更新配置
            config.setConfigValue(reqVO.getConfigValue());
            systemConfigMapper.updateById(config);
        }
        log.info("[updateConfig] 更新配置成功: {} = {}", reqVO.getConfigKey(), reqVO.getConfigValue());
    }

    @Override
    public void setConfigValue(String configKey, String configValue) {
        updateConfig(new SystemConfigUpdateReqVO().setConfigKey(configKey).setConfigValue(configValue));
    }

    private SystemConfigRespVO convertToRespVO(SystemConfigDO config) {
        SystemConfigRespVO respVO = new SystemConfigRespVO();
        respVO.setId(config.getId());
        respVO.setConfigKey(config.getConfigKey());
        respVO.setConfigValue(config.getConfigValue());
        respVO.setConfigType(config.getConfigType());
        respVO.setDescription(config.getDescription());
        respVO.setIsPublic(config.getIsPublic());
        respVO.setCreateTime(config.getCreateTime());
        respVO.setUpdateTime(config.getUpdateTime());
        return respVO;
    }

}
