package cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 模型调用日志 DO
 *
 * @author StudyBuddy
 */
@TableName("study_model_call_log")
@KeySequence("study_model_call_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelCallLogDO extends BaseDO {

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
     * 模型类型(OCR/LLM)
     */
    private String modelType;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 试卷ID
     */
    private Long paperId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 输入Token数
     */
    private Integer inputTokens;

    /**
     * 输出Token数
     */
    private Integer outputTokens;

    /**
     * 总Token数
     */
    private Integer totalTokens;

    /**
     * 响应时间(ms)
     */
    private Integer responseTime;

    /**
     * 状态(success/failed)
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMsg;

}
