package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 试卷创建 Request VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 试卷创建 Request VO")
@Data
public class PaperCreateReqVO {

    @Schema(description = "试卷编号", required = true, example = "P2025001")
    @NotBlank(message = "试卷编号不能为空")
    private String paperNo;

    @Schema(description = "学生ID", required = true, example = "1001")
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @Schema(description = "科目", example = "数学")
    private String subject;

    @Schema(description = "试卷标题", example = "期中考试数学试卷")
    private String title;

    @Schema(description = "考试日期", example = "2025-01-15")
    private LocalDate examDate;

    @Schema(description = "年级", example = "高一")
    private String grade;

    @Schema(description = "学期", example = "第一学期")
    private String semester;

    @Schema(description = "试卷文件路径", example = "/upload/papers/paper001.pdf")
    private String filePath;

}
