package cn.iocoder.yudao.module.studybuddy.framework.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * StudyBuddy 文件存储配置
 *
 * 支持本地存储和 MinIO 对象存储
 *
 * @author StudyBuddy
 */
@Component
@ConfigurationProperties(prefix = "studybuddy.file")
@Data
public class StudyBuddyFileProperties {

    /**
     * 存储类型：local 表示本地存储，minio 表示 MinIO 存储
     */
    private String storage = "local";

    /**
     * 本地存储配置
     */
    private LocalConfig local = new LocalConfig();

    /**
     * MinIO 存储配置
     */
    private MinioConfig minio = new MinioConfig();

    /**
     * 本地存储配置
     */
    @Data
    public static class LocalConfig {
        /**
         * 本地文件存储根目录
         */
        private String basePath = "/home/wxl/data/studybuddy-files";

        /**
         * 本地文件访问域名
         */
        private String domain = "http://localhost:48080";
    }

    /**
     * MinIO 存储配置
     */
    @Data
    public static class MinioConfig {
        /**
         * MinIO 服务端点
         */
        private String endpoint = "http://127.0.0.1:9000";

        /**
         * 存储桶名称
         */
        private String bucket = "studybuddy";

        /**
         * Access Key
         */
        private String accessKey = "";

        /**
         * Secret Key
         */
        private String secretKey = "";

        /**
         * 自定义访问域名（可选）
         */
        private String domain = "";
    }

}