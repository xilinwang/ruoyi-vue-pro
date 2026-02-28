package cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 学习情况汇总 RespVO
 */
@Schema(description = "管理后台 - 学习情况汇总 Response VO")
@Data
public class LearningSummaryRespVO {

    @Schema(description = "总练习题目数", example = "100")
    private Integer totalQuestions;

    @Schema(description = "正确题目数", example = "70")
    private Integer correctCount;

    @Schema(description = "错误题目数", example = "30")
    private Integer wrongCount;

    @Schema(description = "总体正确率", example = "70.00")
    private BigDecimal overallAccuracyRate;

    @Schema(description = "掌握得比较好的知识点")
    private List<KnowledgePointStats> strongPoints;

    @Schema(description = "薄弱知识点")
    private List<KnowledgePointStats> weakPoints;

    @Schema(description = "学习建议")
    private List<String> suggestions;

    @Schema(description = "知识点统计")
    @Data
    public static class KnowledgePointStats {
        private String knowledgePoint;
        private Integer totalCount;
        private Integer correctCount;
        private BigDecimal accuracyRate;
        private String masteryLevel;
    }

}
