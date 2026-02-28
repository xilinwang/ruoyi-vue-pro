package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 试卷 Response VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 试卷 Response VO")
@Data
public class PaperRespVO {

    @Schema(description = "试卷ID", example = "1")
    private Long id;

    @Schema(description = "试卷编号", example = "P2025001")
    private String paperNo;

    @Schema(description = "学生ID", example = "1001")
    private Long studentId;

    @Schema(description = "科目ID", example = "1")
    private Long subjectId;

    @Schema(description = "科目名称", example = "数学")
    private String subject;

    @Schema(description = "试卷标题", example = "期中考试数学试卷")
    private String title;

    @Schema(description = "试卷描述", example = "高一上学期期中考试")
    private String description;

    @Schema(description = "考试日期", example = "2025-01-15")
    private LocalDate examDate;

    @Schema(description = "年级", example = "高一")
    private String grade;

    @Schema(description = "学期", example = "第一学期")
    private String semester;

    @Schema(description = "试卷文件路径（已废弃，使用files）", example = "/upload/papers/paper001.pdf")
    @Deprecated
    private String filePath;

    @Schema(description = "试卷文件列表")
    private List<PaperFileRespVO> files;

    @Schema(description = "题目数量", example = "20")
    private Integer questionCount;

    @Schema(description = "处理状态", example = "READY")
    private String status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "创建时间", example = "2025-01-01 12:00:00")
    private LocalDateTime createTime;

}
