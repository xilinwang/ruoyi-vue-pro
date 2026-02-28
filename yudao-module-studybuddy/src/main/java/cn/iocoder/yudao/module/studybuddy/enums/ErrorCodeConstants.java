package cn.iocoder.yudao.module.studybuddy.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * StudyBuddy 错误码枚举
 * <p>
 * studybuddy 系统错误码，范围：1_020_000_000 ~ 1_029_999_999
 *
 * @author StudyBuddy
 */
public interface ErrorCodeConstants {

    // ========== 试卷模块 1-020-000-000 ==========
    ErrorCode PAPER_NOT_EXISTS = new ErrorCode(1_020_001_000, "试卷不存在");
    ErrorCode PAPER_NO_EXISTS = new ErrorCode(1_020_001_001, "试卷编号已存在");
    ErrorCode PAPER_STATUS_NOT_READY = new ErrorCode(1_020_001_002, "试卷状态未就绪，无法分析");

    // ========== 题目模块 1-021-000-000 ==========
    ErrorCode QUESTION_NOT_EXISTS = new ErrorCode(1_021_001_000, "题目不存在");

    // ========== 科目模块 1-022-000-000 ==========
    ErrorCode SUBJECT_NOT_EXISTS = new ErrorCode(1_022_001_000, "科目不存在");
    ErrorCode SUBJECT_NAME_EXISTS = new ErrorCode(1_022_001_001, "科目名称已存在");

    // ========== 试卷文件模块 1-023-000-000 ==========
    ErrorCode PAPER_FILE_NOT_EXISTS = new ErrorCode(1_023_001_000, "试卷文件不存在");

}
