package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷文件 Response VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 试卷文件 Response VO")
@Data
public class PaperFileRespVO {

    @Schema(description = "文件ID", example = "1")
    private Long id;

    @Schema(description = "试卷ID", example = "1")
    private Long paperId;

    @Schema(description = "文件路径", example = "/upload/papers/page1.jpg")
    private String filePath;

    @Schema(description = "文件名", example = "page1.jpg")
    private String fileName;

    @Schema(description = "文件类型", example = "jpg")
    private String fileType;

    @Schema(description = "文件大小", example = "1024000")
    private Long fileSize;

    @Schema(description = "排序序号", example = "0")
    private Integer sortOrder;

    @Schema(description = "创建时间", example = "2025-01-01 12:00:00")
    private LocalDateTime createTime;

}
