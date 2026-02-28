package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

/**
 * 试卷创建 Request VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 试卷创建 Request VO")
@Data
public class PaperCreateReqVO {

    @Schema(description = "试卷编号", example = "P2025001")
    private String paperNo;

    @Schema(description = "学生ID", example = "1001")
    private Long studentId;

    @Schema(description = "科目ID", example = "1")
    private Long subjectId;

    @Schema(description = "科目名称（冗余字段）", example = "数学")
    private String subject;

    @Schema(description = "试卷标题", required = true, example = "期中考试数学试卷")
    @NotBlank(message = "试卷标题不能为空")
    private String title;

    @Schema(description = "试卷描述")
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
    private List<FileCreateInfo> files;

    /**
     * 文件创建信息
     */
    @Data
    @Schema(description = "文件创建信息")
    public static class FileCreateInfo {
        @Schema(description = "文件路径", required = true)
        private String filePath;

        @Schema(description = "文件名")
        private String fileName;

        @Schema(description = "文件类型")
        private String fileType;

        @Schema(description = "文件大小（字节）")
        private Long fileSize;

        @Schema(description = "排序序号")
        private Integer sortOrder;
    }

}
