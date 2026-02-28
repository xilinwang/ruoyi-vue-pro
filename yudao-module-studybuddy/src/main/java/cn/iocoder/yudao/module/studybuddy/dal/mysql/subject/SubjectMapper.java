package cn.iocoder.yudao.module.studybuddy.dal.mysql.subject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.studybuddy.controller.admin.subject.vo.SubjectPageReqVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.subject.SubjectDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 科目 Mapper
 *
 * @author StudyBuddy
 */
@Mapper
public interface SubjectMapper extends BaseMapperX<SubjectDO> {

    /**
     * 根据用户ID查询科目列表
     *
     * @param userId 用户ID
     * @return 科目列表
     */
    default List<SubjectDO> selectListByUserId(Long userId) {
        return selectList(SubjectDO::getUserId, userId);
    }

    /**
     * 根据用户ID和科目名称查询
     *
     * @param userId 用户ID
     * @param name 科目名称
     * @return 科目DO
     */
    default SubjectDO selectByUserIdAndName(Long userId, String name) {
        return selectOne(SubjectDO::getUserId, userId, SubjectDO::getName, name);
    }

    /**
     * 分页查询科目
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<SubjectDO> selectPage(SubjectPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SubjectDO>()
                .eqIfPresent(SubjectDO::getUserId, reqVO.getUserId())
                .likeIfPresent(SubjectDO::getName, reqVO.getName())
                .betweenIfPresent(SubjectDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SubjectDO::getId));
    }

}
