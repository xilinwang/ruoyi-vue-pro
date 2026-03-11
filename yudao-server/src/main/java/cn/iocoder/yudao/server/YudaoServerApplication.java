package cn.iocoder.yudao.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目的启动类
 *
 * 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
 *
 * @author 芋道源码
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${yudao.info.base-package}
@SpringBootApplication(scanBasePackages = {"${yudao.info.base-package}.server", "${yudao.info.base-package}.module"})
public class YudaoServerApplication {

    public static void main(String[] args) {
        // 加载 .env 文件中的环境变量（在 Spring 启动之前）
        loadDotenv();

        // 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章

        SpringApplication.run(YudaoServerApplication.class, args);

        // 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
    }

    /**
     * 加载 .env 文件中的环境变量
     * 从项目根目录查找 .env 文件
     * 设置到系统属性中，使 Spring Boot 能通过 ${VAR:} 语法读取
     */
    private static void loadDotenv() {
        try {
            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            File rootDir = new File(projectRoot);

            // 如果当前目录是 yudao-server，需要向上查找项目根目录
            // yudao-server 在 framework/ruoyi-vue-pro/yudao-server 目录下
            // 所以需要向上 3 级才能到达 StudyBuddy 根目录
            if (projectRoot.endsWith("yudao-server")) {
                rootDir = new File(projectRoot).getParentFile().getParentFile().getParentFile();
            }

            File envFile = new File(rootDir, ".env");
            System.out.println("[Dotenv] 项目根目录: " + rootDir.getAbsolutePath());
            System.out.println("[Dotenv] 查找 .env 文件: " + envFile.getAbsolutePath());

            if (!envFile.exists()) {
                System.out.println("[Dotenv] .env 文件不存在，跳过加载");
                return;
            }

            // 直接读取 .env 文件（更可靠）
            Map<String, String> envVars = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    // 跳过空行和注释
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    // 解析 KEY=VALUE 格式
                    int eqIndex = line.indexOf('=');
                    if (eqIndex > 0) {
                        String key = line.substring(0, eqIndex).trim();
                        String value = line.substring(eqIndex + 1).trim();
                        // 移除可能的引号
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        envVars.put(key, value);
                    }
                }
            }

            System.out.println("[Dotenv] 从文件读取到 " + envVars.size() + " 个变量");

            // 设置关键配置变量
            String[] keys = {
                "ALIYUN_ACCESS_KEY_ID",
                "ALIYUN_ACCESS_KEY_SECRET",
                "DEEPSEEK_API_KEY",
                "DEEPSEEK_BASE_URL",
                "DEEPSEEK_MODEL",
                "QWEN_API_KEY",
                "QWEN_BASE_URL",
                "QWEN_MODEL",
                "DB_USERNAME",
                "DB_PASSWORD",
                "FILE_STORAGE",
                "FILE_LOCAL_PATH",
                "FILE_LOCAL_DOMAIN"
            };

            int count = 0;
            for (String key : keys) {
                String value = envVars.get(key);
                if (value != null && !value.isEmpty()) {
                    System.setProperty(key, value);
                    count++;
                    // 打印设置的变量（敏感信息打码）
                    boolean isSensitive = key.contains("KEY") || key.contains("SECRET") || key.contains("PASSWORD");
                    System.out.println("[Dotenv] 设置系统属性: " + key + " = " +
                            (isSensitive ? value.substring(0, Math.min(4, value.length())) + "****" : value));
                }
            }

            System.out.println("[Dotenv] 成功加载 .env 文件，设置系统属性数: " + count);

        } catch (Exception e) {
            System.out.println("[Dotenv] 加载 .env 文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

}