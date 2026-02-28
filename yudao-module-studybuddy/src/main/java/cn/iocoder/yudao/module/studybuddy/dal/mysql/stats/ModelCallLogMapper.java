package cn.iocoder.yudao.module.studybuddy.dal.mysql.stats;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.ModelCallLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 模型调用日志 Mapper
 *
 * @author StudyBuddy
 */
@Mapper
public interface ModelCallLogMapper extends BaseMapperX<ModelCallLogDO> {

    /**
     * 按模型类型统计调用次数
     */
    @Select("SELECT model_type, COUNT(*) as count, AVG(response_time) as avg_time FROM study_model_call_log " +
            "WHERE create_time >= #{startTime} AND create_time <= #{endTime} AND deleted = false " +
            "GROUP BY model_type")
    List<Map<String, Object>> countByModelType(@Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 按模型名称统计调用次数
     */
    @Select("SELECT model_name, model_type, COUNT(*) as count, SUM(total_tokens) as total_tokens, " +
            "AVG(response_time) as avg_time FROM study_model_call_log " +
            "WHERE create_time >= #{startTime} AND create_time <= #{endTime} AND deleted = false " +
            "GROUP BY model_name, model_type ORDER BY count DESC")
    List<Map<String, Object>> countByModelName(@Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 按日期统计调用次数
     */
    @Select("SELECT DATE(create_time) as date, model_type, COUNT(*) as count FROM study_model_call_log " +
            "WHERE create_time >= #{startTime} AND create_time <= #{endTime} AND deleted = false " +
            "GROUP BY DATE(create_time), model_type ORDER BY date")
    List<Map<String, Object>> countByDate(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 统计成功率
     */
    @Select("SELECT model_type, " +
            "SUM(CASE WHEN status = 'success' THEN 1 ELSE 0 END) as success_count, " +
            "COUNT(*) as total_count " +
            "FROM study_model_call_log " +
            "WHERE create_time >= #{startTime} AND create_time <= #{endTime} AND deleted = false " +
            "GROUP BY model_type")
    List<Map<String, Object>> getSuccessRate(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

}
