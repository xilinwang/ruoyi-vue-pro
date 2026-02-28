package cn.iocoder.yudao.module.studybuddy.dal.mysql.stats;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookPageReqVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.WrongBookDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 错题本 Mapper
 *
 * @author StudyBuddy
 */
@Mapper
public interface WrongBookMapper extends BaseMapperX<WrongBookDO> {

    default PageResult<WrongBookDO> selectPage(Long userId, WrongBookPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WrongBookDO>()
                .eqIfPresent(WrongBookDO::getUserId, userId)
                .eqIfPresent(WrongBookDO::getPaperId, reqVO.getPaperId())
                .likeIfPresent(WrongBookDO::getKnowledgePoint, reqVO.getKnowledgePoint())
                .eqIfPresent(WrongBookDO::getIsMastered, reqVO.getIsMastered())
                .orderByDesc(WrongBookDO::getLastWrongTime));
    }

    default List<WrongBookDO> selectByUserId(Long userId) {
        return selectList(WrongBookDO::getUserId, userId);
    }

    default WrongBookDO selectByUserAndQuestion(Long userId, Long questionId) {
        return selectOne(new LambdaQueryWrapperX<WrongBookDO>()
                .eq(WrongBookDO::getUserId, userId)
                .eq(WrongBookDO::getQuestionId, questionId));
    }

    default List<WrongBookDO> selectByIds(Long userId, List<Long> ids) {
        return selectList(new LambdaQueryWrapperX<WrongBookDO>()
                .eq(WrongBookDO::getUserId, userId)
                .in(WrongBookDO::getId, ids));
    }

}
