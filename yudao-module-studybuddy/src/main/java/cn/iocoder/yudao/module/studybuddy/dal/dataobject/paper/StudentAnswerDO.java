package cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 学生答案 DO
 *
 * @author StudyBuddy
 */
@TableName("study_question_student_answer")
@KeySequence("study_question_student_answer_seq") // PostgreSQL 序列
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 题目ID，外键关联study_question表
     */
    private Long questionId;

    /**
     * 学生原始答案
     */
    private String rawAnswer;

    /**
     * 是否正确：true-正确，false-错误
     */
    private Boolean isCorrect;

    /**
     * 错误分析（LLM生成）
     */
    private String errorAnalysis;

    /**
     * 更优解法（LLM生成）
     */
    private String betterSolution;

    /**
     * 答案来源：MANUAL-手动输入，OCR-OCR识别
     */
    private String source;

}
