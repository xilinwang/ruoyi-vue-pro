package cn.iocoder.yudao.module.studybuddy.enums.ocr;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OCR 模型枚举
 *
 * @author StudyBuddy
 */
@Getter
@AllArgsConstructor
public enum OcrModelEnum {

    /**
     * 阿里云读光 OCR
     */
    ALIYUN("aliyun", "阿里云读光"),

    /**
     * iFlow OCR (通义千问 VL)
     */
    IFLOW("iflow", "iFlow 视觉模型");

    /**
     * 模型代码
     */
    private final String code;

    /**
     * 模型名称
     */
    private final String name;

    /**
     * 根据代码获取枚举
     */
    public static OcrModelEnum getByCode(String code) {
        for (OcrModelEnum model : values()) {
            if (model.getCode().equals(code)) {
                return model;
            }
        }
        return ALIYUN; // 默认使用阿里云
    }

}
