package cn.iocoder.yudao.module.studybuddy.dal.mysql.paper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.StudentAnswerDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生答案 Mapper
 *
 * @author StudyBuddy
 */
@Mapper
public interface StudentAnswerMapper extends BaseMapperX<StudentAnswerDO> {

    /**
     * 根据题目ID查询学生答案
     *
     * @param questionId 题目ID
     * @return 学生答案DO
     */
    default StudentAnswerDO selectByQuestionId(Long questionId) {
        return selectOne(StudentAnswerDO::getQuestionId, questionId);
    }

}
