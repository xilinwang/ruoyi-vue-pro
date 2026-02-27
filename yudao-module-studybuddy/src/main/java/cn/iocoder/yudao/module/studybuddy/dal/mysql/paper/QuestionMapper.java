package cn.iocoder.yudao.module.studybuddy.dal.mysql.paper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.QuestionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 题目 Mapper
 *
 * @author StudyBuddy
 */
@Mapper
public interface QuestionMapper extends BaseMapperX<QuestionDO> {

    /**
     * 根据试卷ID查询题目列表
     *
     * @param paperId 试卷ID
     * @return 题目列表
     */
    default List<QuestionDO> selectListByPaperId(Long paperId) {
        return selectList(QuestionDO::getPaperId, paperId);
    }

    /**
     * 根据题目编号查询题目
     *
     * @param questionNo 题目编号
     * @return 题目DO
     */
    default QuestionDO selectByQuestionNo(String questionNo) {
        return selectOne(QuestionDO::getQuestionNo, questionNo);
    }

    /**
     * 根据试卷ID和题目编号查询题目
     *
     * @param paperId    试卷ID
     * @param questionNo 题目编号
     * @return 题目DO
     */
    default QuestionDO selectByPaperIdAndQuestionNo(Long paperId, String questionNo) {
        return selectOne(new LambdaQueryWrapperX<QuestionDO>()
                .eq(QuestionDO::getPaperId, paperId)
                .eq(QuestionDO::getQuestionNo, questionNo));
    }

}
