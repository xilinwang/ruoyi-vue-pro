package cn.iocoder.yudao.module.studybuddy.framework.ocr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OCR 配置类
 *
 * @author StudyBuddy
 */
@Component
@ConfigurationProperties(prefix = "studybuddy.ocr.aliyun")
@Data
public class OcrConfiguration {

    /**
     * Access Key ID
     */
    private String accessKeyId;

    /**
     * Access Key Secret
     */
    private String accessKeySecret;

    /**
     * 服务端点
     */
    private String endpoint = "https://ocr-api.cn-shanghai.aliyuncs.com";

}
