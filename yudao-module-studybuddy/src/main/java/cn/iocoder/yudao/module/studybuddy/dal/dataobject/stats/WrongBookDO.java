package cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 错题本 DO
 *
 * @author StudyBuddy
 */
@TableName("study_wrong_book")
@KeySequence("study_wrong_book_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongBookDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 试卷ID
     */
    private Long paperId;

    /**
     * 题号
     */
    private String questionNo;

    /**
     * 题目内容
     */
    private String questionContent;

    /**
     * 知识点
     */
    private String knowledgePoint;

    /**
     * 学生答案
     */
    private String studentAnswer;

    /**
     * 标准答案
     */
    private String standardAnswer;

    /**
     * 错误分析
     */
    private String errorAnalysis;

    /**
     * 更优解法
     */
    private String betterSolution;

    /**
     * 错误次数
     */
    private Integer wrongCount;

    /**
     * 最后错误时间
     */
    private LocalDateTime lastWrongTime;

    /**
     * 是否已掌握
     */
    private Boolean isMastered;

    /**
     * 掌握时间
     */
    private LocalDateTime masteredTime;

    /**
     * 备注
     */
    private String remark;

}
