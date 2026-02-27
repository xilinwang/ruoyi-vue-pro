package cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.studybuddy.framework.mybatis.type.PostgresJsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 题目 DO
 *
 * @author StudyBuddy
 */
@TableName("study_question")
@KeySequence("study_question_seq") // PostgreSQL 序列
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 试卷ID，外键关联study_paper表
     */
    private Long paperId;

    /**
     * 题目编号
     */
    private String questionNo;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 知识点
     */
    private String knowledgePoint;

    /**
     * 题目在试卷中的位置信息（JSON格式）
     *
     * 包含：页码、位置坐标、题目类型等信息
     * 注意：PostgreSQL jsonb 类型，使用 String 存储
     */
    @TableField(typeHandler = PostgresJsonbTypeHandler.class)
    private String positionJson;

    /**
     * 标准答案（LLM自动提取或教师手动录入）
     */
    private String standardAnswer;

    /**
     * 标准答案是否已由教师审核确认
     */
    private Boolean standardAnswerVerified;

    /**
     * 原始答案（从试卷中提取的原始答案文本）
     */
    private String originalAnswer;

    /**
     * 解题思路（AI生成或教师提供的解题思路）
     */
    private String solutionApproach;

}
