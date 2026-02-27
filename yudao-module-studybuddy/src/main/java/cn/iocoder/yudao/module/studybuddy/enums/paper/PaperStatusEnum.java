package cn.iocoder.yudao.module.studybuddy.enums.paper;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 试卷处理状态枚举
 *
 * @author StudyBuddy
 */
@Getter
@AllArgsConstructor
public enum PaperStatusEnum {

    /**
     * OCR 处理中
     */
    OCR_PROCESSING("OCR_PROCESSING", "OCR 处理中"),

    /**
     * OCR 失败
     */
    OCR_FAILED("OCR_FAILED", "OCR 失败"),

    /**
     * 已就绪，可以进行解析
     */
    READY("READY", "已就绪"),

    /**
     * 正在分析
     */
    ANALYZING("ANALYZING", "正在分析"),

    /**
     * 分析完成
     */
    ANALYZED("ANALYZED", "分析完成"),

    /**
     * 分析失败
     */
    ANALYSIS_FAILED("ANALYSIS_FAILED", "分析失败");

    /**
     * 状态码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 枚举值
     */
    public static PaperStatusEnum fromCode(String code) {
        for (PaperStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
