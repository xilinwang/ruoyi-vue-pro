package cn.iocoder.yudao.module.studybuddy.controller.admin.subject.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 科目 Response VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 科目 Response VO")
@Data
public class SubjectRespVO {

    @Schema(description = "科目ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "科目名称", example = "数学")
    private String name;

    @Schema(description = "科目描述", example = "高中数学课程")
    private String description;

    @Schema(description = "创建时间", example = "2025-01-01 12:00:00")
    private LocalDateTime createTime;

}
