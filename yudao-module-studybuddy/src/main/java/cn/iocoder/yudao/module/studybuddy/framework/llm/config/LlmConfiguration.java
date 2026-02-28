package cn.iocoder.yudao.module.studybuddy.framework.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * LLM 配置类
 * 支持多模型提供商（deepseek 和 qwen）
 *
 * @author StudyBuddy
 */
@Component
@ConfigurationProperties(prefix = "studybuddy.llm")
@Data
public class LlmConfiguration {

    /**
     * 当前使用的提供商（deepseek 或 qwen）
     */
    private String provider = "deepseek";

    /**
     * DeepSeek 配置
     */
    private ProviderConfig deepseek = new ProviderConfig();

    /**
     * Qwen 配置
     */
    private ProviderConfig qwen = new ProviderConfig();

    /**
     * 超时时间（秒）
     */
    private Integer timeout = 120;

    /**
     * 提供商配置
     */
    @Data
    public static class ProviderConfig {
        /**
         * API Key
         */
        private String apiKey;

        /**
         * API Base URL
         */
        private String baseUrl;

        /**
         * 模型名称
         */
        private String model;
    }

    /**
     * 获取当前提供商的配置
     */
    public ProviderConfig getCurrentProviderConfig() {
        if ("qwen".equalsIgnoreCase(provider)) {
            return qwen;
        }
        return deepseek;
    }

    /**
     * 获取当前 API Key
     */
    public String getApiKey() {
        return getCurrentProviderConfig().getApiKey();
    }

    /**
     * 获取当前 Base URL
     */
    public String getBaseUrl() {
        ProviderConfig config = getCurrentProviderConfig();
        return config.getBaseUrl() != null ? config.getBaseUrl() : getDefaultBaseUrl();
    }

    /**
     * 获取当前模型名称
     */
    public String getModel() {
        ProviderConfig config = getCurrentProviderConfig();
        return config.getModel() != null ? config.getModel() : getDefaultModel();
    }

    private String getDefaultBaseUrl() {
        if ("qwen".equalsIgnoreCase(provider)) {
            return "https://apis.iflow.cn/v1";
        }
        return "https://api.deepseek.com";
    }

    private String getDefaultModel() {
        if ("qwen".equalsIgnoreCase(provider)) {
            return "qwen3-max";
        }
        return "deepseek-chat";
    }

}
