package cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.studybuddy.enums.paper.PaperStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 试卷 DO
 *
 * @author StudyBuddy
 */
@TableName("study_paper")
@KeySequence("study_paper_seq") // PostgreSQL 序列
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaperDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 试卷编号，唯一标识
     */
    private String paperNo;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 科目ID
     */
    private Long subjectId;

    /**
     * 科目名称（冗余字段，用于显示）
     */
    private String subject;

    /**
     * 试卷标题
     */
    private String title;

    /**
     * 试卷描述
     */
    private String description;

    /**
     * 考试日期
     */
    private LocalDate examDate;

    /**
     * 年级
     */
    private String grade;

    /**
     * 学期
     */
    private String semester;

    /**
     * 试卷文件存储路径
     */
    private String filePath;

    /**
     * 处理状态
     *
     * 枚举 {@link PaperStatusEnum}
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * OCR 模型 (aliyun/iflow)
     */
    private String ocrModel;

}
