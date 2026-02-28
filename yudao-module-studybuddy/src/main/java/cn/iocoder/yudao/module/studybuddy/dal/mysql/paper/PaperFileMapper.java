package cn.iocoder.yudao.module.studybuddy.dal.mysql.paper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperFileDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 试卷文件 Mapper
 *
 * @author StudyBuddy
 */
@Mapper
public interface PaperFileMapper extends BaseMapperX<PaperFileDO> {

    /**
     * 根据试卷ID查询文件列表
     *
     * @param paperId 试卷ID
     * @return 文件列表
     */
    default List<PaperFileDO> selectListByPaperId(Long paperId) {
        return selectList(new LambdaQueryWrapperX<PaperFileDO>()
                .eq(PaperFileDO::getPaperId, paperId)
                .orderByAsc(PaperFileDO::getSortOrder));
    }

    /**
     * 根据试卷ID删除所有文件
     *
     * @param paperId 试卷ID
     */
    default void deleteByPaperId(Long paperId) {
        delete(PaperFileDO::getPaperId, paperId);
    }

}
