package cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学习统计 DO
 *
 * @author StudyBuddy
 */
@TableName("study_learning_stats")
@KeySequence("study_learning_stats_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningStatsDO extends BaseDO {

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
     * 知识点
     */
    private String knowledgePoint;

    /**
     * 总练习次数
     */
    private Integer totalCount;

    /**
     * 正确次数
     */
    private Integer correctCount;

    /**
     * 错误次数
     */
    private Integer wrongCount;

    /**
     * 正确率
     */
    private BigDecimal accuracyRate;

    /**
     * 掌握程度(WEAK/NORMAL/GOOD/EXCELLENT)
     */
    private String masteryLevel;

    /**
     * 最后练习时间
     */
    private LocalDateTime lastPracticeTime;

}
