package cn.iocoder.yudao.module.studybuddy.framework.ocr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * iFlow OCR 配置类
 *
 * 使用通义千问 VL 模型进行视觉识别
 *
 * @author StudyBuddy
 */
@Component
@ConfigurationProperties(prefix = "studybuddy.ocr.iflow")
@Data
public class IflowOcrConfiguration {

    /**
     * API Base URL
     */
    private String baseUrl = "https://apis.iflow.cn/v1";

    /**
     * API Key
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String model = "qwen3-vl-plus";

}
