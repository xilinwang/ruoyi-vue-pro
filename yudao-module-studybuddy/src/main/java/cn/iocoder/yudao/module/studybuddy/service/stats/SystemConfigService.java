package cn.iocoder.yudao.module.studybuddy.service.stats;

import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.SystemConfigRespVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.SystemConfigUpdateReqVO;

import java.util.List;

/**
 * 系统配置 Service 接口
 *
 * @author StudyBuddy
 */
public interface SystemConfigService {

    /**
     * 获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 获取所有公开配置
     *
     * @return 公开配置列表
     */
    List<SystemConfigRespVO> getPublicConfigs();

    /**
     * 获取所有配置
     *
     * @return 配置列表
     */
    List<SystemConfigRespVO> getAllConfigs();

    /**
     * 更新配置
     *
     * @param reqVO 更新请求
     */
    void updateConfig(SystemConfigUpdateReqVO reqVO);

    /**
     * 设置配置值
     *
     * @param configKey   配置键
     * @param configValue 配置值
     */
    void setConfigValue(String configKey, String configValue);

}
