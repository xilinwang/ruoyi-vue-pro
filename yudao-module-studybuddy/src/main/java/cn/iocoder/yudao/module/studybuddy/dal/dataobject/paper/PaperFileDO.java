package cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 试卷文件 DO
 *
 * @author StudyBuddy
 */
@TableName("study_paper_file")
@KeySequence("study_paper_file_id_seq") // PostgreSQL 序列
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaperFileDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 试卷ID
     */
    private Long paperId;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型 (pdf, jpg, png等)
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 排序序号
     */
    private Integer sortOrder;

}
