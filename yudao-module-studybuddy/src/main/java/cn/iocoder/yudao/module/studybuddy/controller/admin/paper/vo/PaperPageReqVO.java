package cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

/**
 * 试卷分页查询 Request VO
 *
 * @author StudyBuddy
 */
@Schema(description = "管理后台 - 试卷分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PaperPageReqVO extends PageParam {

    @Schema(description = "试卷编号", example = "P2025001")
    private String paperNo;

    @Schema(description = "试卷标题", example = "期中考试数学试卷")
    private String title;

    @Schema(description = "学生ID", example = "1001")
    private Long studentId;

    @Schema(description = "科目ID", example = "1")
    private Long subjectId;

    @Schema(description = "科目", example = "数学")
    private String subject;

    @Schema(description = "处理状态", example = "READY")
    private String status;

    @Schema(description = "考试日期起始", example = "2025-01-01")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] examDate;

    @Schema(description = "创建时间起始", example = "2025-01-01 00:00:00")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
