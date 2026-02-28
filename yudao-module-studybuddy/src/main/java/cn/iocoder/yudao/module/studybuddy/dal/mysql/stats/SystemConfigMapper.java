package cn.iocoder.yudao.module.studybuddy.dal.mysql.stats;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.SystemConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置 Mapper
 *
 * @author StudyBuddy
 */
@Mapper
public interface SystemConfigMapper extends BaseMapperX<SystemConfigDO> {

    default SystemConfigDO selectByConfigKey(String configKey) {
        return selectOne(SystemConfigDO::getConfigKey, configKey);
    }

}
