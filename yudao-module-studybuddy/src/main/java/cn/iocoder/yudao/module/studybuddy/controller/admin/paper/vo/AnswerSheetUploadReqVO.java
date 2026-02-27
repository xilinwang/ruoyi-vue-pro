package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 答题卡上传 Request VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 答题卡上传 Request VO")
@Data
public class AnswerSheetUploadReqVO {

    @Schema(description = "试卷ID", required = true, example = "1")
    @NotNull(message = "试卷ID不能为空")
    private Long paperId;

    @Schema(description = "学生ID", required = true, example = "1001")
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @Schema(description = "答题卡文件路径", required = true, example = "/upload/answers/sheet001.pdf")
    @NotBlank(message = "答题卡文件路径不能为空")
    private String filePath;

}
