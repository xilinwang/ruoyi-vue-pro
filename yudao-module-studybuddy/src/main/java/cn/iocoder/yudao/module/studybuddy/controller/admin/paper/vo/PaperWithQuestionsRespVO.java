package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 试卷详情（含题目） Response VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 试卷详情（含题目） Response VO")
@Data
@Builder
public class PaperWithQuestionsRespVO {

    @Schema(description = "试卷ID", example = "1")
    private Long id;

    @Schema(description = "试卷编号", example = "P2025001")
    private String paperNo;

    @Schema(description = "学生ID", example = "1001")
    private Long studentId;

    @Schema(description = "科目", example = "数学")
    private String subject;

    @Schema(description = "试卷标题", example = "期中考试数学试卷")
    private String title;

    @Schema(description = "试卷描述")
    private String description;

    @Schema(description = "考试日期", example = "2025-01-15")
    private LocalDate examDate;

    @Schema(description = "年级", example = "高一")
    private String grade;

    @Schema(description = "学期", example = "第一学期")
    private String semester;

    @Schema(description = "试卷文件路径", example = "/upload/papers/paper001.pdf")
    private String filePath;

    @Schema(description = "试卷状态", example = "READY")
    private String status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "题目数量")
    private Integer questionCount;

    @Schema(description = "题目列表")
    private List<QuestionRespVO> questions;

}
