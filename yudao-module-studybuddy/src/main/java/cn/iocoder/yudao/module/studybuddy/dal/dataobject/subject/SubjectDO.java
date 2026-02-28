package cn.iocoder.yudao.module.studybuddy.dal.dataobject.subject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 科目 DO
 *
 * @author StudyBuddy
 */
@TableName("study_subject")
@KeySequence("study_subject_id_seq") // PostgreSQL 序列
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 科目名称
     */
    private String name;

    /**
     * 科目描述
     */
    private String description;

}
