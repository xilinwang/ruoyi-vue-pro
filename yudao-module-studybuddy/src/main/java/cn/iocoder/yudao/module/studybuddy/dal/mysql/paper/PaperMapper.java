package cn.iocoder.yudao.module.studybuddy.dal.mysql.paper;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.PaperPageReqVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperDO;
import cn.iocoder.yudao.module.studybuddy.enums.paper.PaperStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 试卷 Mapper
 *
 * @author StudyBuddy
 */
@Mapper
public interface PaperMapper extends BaseMapperX<PaperDO> {

    /**
     * 根据试卷编号查询试卷
     *
     * @param paperNo 试卷编号
     * @return 试卷DO
     */
    default PaperDO selectByPaperNo(String paperNo) {
        return selectOne(PaperDO::getPaperNo, paperNo);
    }

    /**
     * 根据学生ID查询试卷列表
     *
     * @param studentId 学生ID
     * @return 试卷列表
     */
    default List<PaperDO> selectListByStudentId(Long studentId) {
        return selectList(PaperDO::getStudentId, studentId);
    }

    /**
     * 根据状态查询试卷列表
     *
     * @param status 状态
     * @return 试卷列表
     */
    default List<PaperDO> selectListByStatus(String status) {
        return selectList(PaperDO::getStatus, status);
    }

    /**
     * 分页查询试卷
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<PaperDO> selectPage(PaperPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PaperDO>()
                .likeIfPresent(PaperDO::getPaperNo, reqVO.getPaperNo())
                .likeIfPresent(PaperDO::getTitle, reqVO.getTitle())
                .eqIfPresent(PaperDO::getStudentId, reqVO.getStudentId())
                .eqIfPresent(PaperDO::getSubjectId, reqVO.getSubjectId())
                .likeIfPresent(PaperDO::getSubject, reqVO.getSubject())
                .eqIfPresent(PaperDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PaperDO::getExamDate, reqVO.getExamDate())
                .betweenIfPresent(PaperDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PaperDO::getId));
    }

}
