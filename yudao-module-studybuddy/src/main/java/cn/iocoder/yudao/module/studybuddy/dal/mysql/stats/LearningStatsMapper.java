package cn.iocoder.yudao.module.studybuddy.dal.mysql.stats;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.LearningStatsDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 学习统计 Mapper
 *
 * @author StudyBuddy
 */
@Mapper
public interface LearningStatsMapper extends BaseMapperX<LearningStatsDO> {

    default List<LearningStatsDO> selectByUserId(Long userId) {
        return selectList(LearningStatsDO::getUserId, userId);
    }

    default LearningStatsDO selectByUserAndKnowledgePoint(Long userId, String knowledgePoint) {
        return selectOne(new LambdaQueryWrapperX<LearningStatsDO>()
                .eq(LearningStatsDO::getUserId, userId)
                .eq(LearningStatsDO::getKnowledgePoint, knowledgePoint));
    }

    default List<LearningStatsDO> selectByMasteryLevel(Long userId, String masteryLevel) {
        return selectList(new LambdaQueryWrapperX<LearningStatsDO>()
                .eq(LearningStatsDO::getUserId, userId)
                .eq(LearningStatsDO::getMasteryLevel, masteryLevel));
    }

}
