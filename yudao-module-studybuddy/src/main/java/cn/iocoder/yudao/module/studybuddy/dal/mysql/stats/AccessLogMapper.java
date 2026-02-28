package cn.iocoder.yudao.module.studybuddy.dal.mysql.stats;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.AccessLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 访问日志 Mapper
 *
 * @author StudyBuddy
 */
@Mapper
public interface AccessLogMapper extends BaseMapperX<AccessLogDO> {

    /**
     * 按模块统计访问次数
     */
    @Select("SELECT module, COUNT(*) as count FROM study_access_log " +
            "WHERE create_time >= #{startTime} AND create_time <= #{endTime} AND deleted = false " +
            "GROUP BY module ORDER BY count DESC")
    List<Map<String, Object>> countByModule(@Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 按用户统计访问次数
     */
    @Select("SELECT user_id, username, COUNT(*) as count FROM study_access_log " +
            "WHERE create_time >= #{startTime} AND create_time <= #{endTime} AND deleted = false " +
            "GROUP BY user_id, username ORDER BY count DESC LIMIT #{limit}")
    List<Map<String, Object>> countByUser(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime,
                                          @Param("limit") int limit);

    /**
     * 按日期统计访问次数
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as count FROM study_access_log " +
            "WHERE create_time >= #{startTime} AND create_time <= #{endTime} AND deleted = false " +
            "GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> countByDate(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 统计总访问次数
     */
    @Select("SELECT COUNT(*) FROM study_access_log WHERE deleted = false")
    Long countTotal();

}
