package cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 系统配置 DO
 *
 * @author StudyBuddy
 */
@TableName("study_system_config")
@KeySequence("study_system_config_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置类型(STRING/NUMBER/BOOLEAN/JSON)
     */
    private String configType;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 是否公开(前端可见)
     */
    private Boolean isPublic;

}
