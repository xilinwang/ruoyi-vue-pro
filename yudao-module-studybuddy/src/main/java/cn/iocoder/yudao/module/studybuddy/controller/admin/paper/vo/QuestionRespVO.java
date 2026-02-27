package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目 Response VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 题目 Response VO")
@Data
public class QuestionRespVO {

    @Schema(description = "题目ID", example = "1")
    private Long id;

    @Schema(description = "试卷ID", example = "1")
    private Long paperId;

    @Schema(description = "题目编号", example = "1")
    private String questionNo;

    @Schema(description = "题目内容", example = "下列关于 Java 中 String 类的说法，正确的是（）")
    private String content;

    @Schema(description = "知识点", example = "String类, 基础语法")
    private String knowledgePoint;

    @Schema(description = "位置信息（JSON）")
    private String positionJson;

    @Schema(description = "标准答案", example = "B")
    private String standardAnswer;

    @Schema(description = "标准答案是否已确认", example = "false")
    private Boolean standardAnswerVerified;

    @Schema(description = "原始答案（从试卷中提取的原始答案文本）")
    private String originalAnswer;

    @Schema(description = "解题思路（AI生成或教师提供的解题思路）")
    private String solutionApproach;

    @Schema(description = "创建时间", example = "2025-01-01 12:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-01-01 12:00:00")
    private LocalDateTime updateTime;

}
