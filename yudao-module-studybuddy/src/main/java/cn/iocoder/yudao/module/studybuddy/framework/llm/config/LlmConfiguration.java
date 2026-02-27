package cn.iocoder.yudao.module.studybuddy.framework.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * LLM 配置类
 *
 * @author StudyBuddy
 */
@Component
@ConfigurationProperties(prefix = "studybuddy.llm.deepseek")
@Data
public class LlmConfiguration {

    /**
     * API Key
     */
    private String apiKey;

    /**
     * API Base URL
     */
    private String baseUrl = "https://api.deepseek.com";

    /**
     * 模型名称
     */
    private String model = "deepseek-chat";

    /**
     * 超时时间（秒）
     */
    private Integer timeout = 120;

}
