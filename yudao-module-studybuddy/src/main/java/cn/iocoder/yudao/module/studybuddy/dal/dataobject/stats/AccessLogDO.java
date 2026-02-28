package cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 访问日志 DO
 *
 * @author StudyBuddy
 */
@TableName("study_access_log")
@KeySequence("study_access_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessLogDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 模块名称
     */
    private String module;

    /**
     * 操作名称
     */
    private String operation;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应状态码
     */
    private Integer responseCode;

    /**
     * 响应时间(ms)
     */
    private Integer responseTime;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * User Agent
     */
    private String userAgent;

}
