package cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学习统计 RespVO
 */
@Schema(description = "管理后台 - 学习统计 Response VO")
@Data
public class LearningStatsRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "知识点", example = "函数")
    private String knowledgePoint;

    @Schema(description = "总练习次数", example = "10")
    private Integer totalCount;

    @Schema(description = "正确次数", example = "7")
    private Integer correctCount;

    @Schema(description = "错误次数", example = "3")
    private Integer wrongCount;

    @Schema(description = "正确率", example = "70.00")
    private BigDecimal accuracyRate;

    @Schema(description = "掌握程度", example = "NORMAL")
    private String masteryLevel;

    @Schema(description = "掌握程度描述", example = "一般")
    private String masteryLevelDesc;

    @Schema(description = "最后练习时间")
    private LocalDateTime lastPracticeTime;

}
