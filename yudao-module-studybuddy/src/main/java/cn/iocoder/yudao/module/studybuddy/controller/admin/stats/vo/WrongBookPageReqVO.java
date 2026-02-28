package cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 错题本分页 ReqVO
 */
@Schema(description = "管理后台 - 错题本分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class WrongBookPageReqVO extends PageParam {

    @Schema(description = "试卷ID", example = "1")
    private Long paperId;

    @Schema(description = "知识点", example = "函数")
    private String knowledgePoint;

    @Schema(description = "是否已掌握", example = "false")
    private Boolean isMastered;

}
