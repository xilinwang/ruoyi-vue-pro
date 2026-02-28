package cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 错题本 RespVO
 */
@Schema(description = "管理后台 - 错题本 Response VO")
@Data
public class WrongBookRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "题目ID", example = "1")
    private Long questionId;

    @Schema(description = "试卷ID", example = "1")
    private Long paperId;

    @Schema(description = "题号", example = "1")
    private String questionNo;

    @Schema(description = "题目内容")
    private String questionContent;

    @Schema(description = "知识点", example = "函数")
    private String knowledgePoint;

    @Schema(description = "学生答案")
    private String studentAnswer;

    @Schema(description = "标准答案")
    private String standardAnswer;

    @Schema(description = "错误分析")
    private String errorAnalysis;

    @Schema(description = "更优解法")
    private String betterSolution;

    @Schema(description = "错误次数", example = "2")
    private Integer wrongCount;

    @Schema(description = "最后错误时间")
    private LocalDateTime lastWrongTime;

    @Schema(description = "是否已掌握", example = "false")
    private Boolean isMastered;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
