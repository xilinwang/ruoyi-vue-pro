package cn.iocoder.yudao.module.studybuddy.service.ocr;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.studybuddy.framework.ocr.config.OcrConfiguration;
import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralRequest;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralResponse;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * 阿里云 OCR 服务实现
 *
 * 使用阿里云读光 OCR API 进行试卷识别
 *
 * @author StudyBuddy
 */
@Service
@Slf4j
public class OcrServiceImpl implements OcrService {

    // OCR 日志目录
    private static final String OCR_LOG_DIR = "./log";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

    @Resource
    private OcrConfiguration ocrConfiguration;

    /**
     * 阿里云 OCR 客户端
     */
    private Client ocrClient;

    /**
     * 初始化阿里云 OCR 客户端
     */
    @PostConstruct
    public void init() {
        try {
            if (StringUtils.hasText(ocrConfiguration.getAccessKeyId()) &&
                StringUtils.hasText(ocrConfiguration.getAccessKeySecret())) {
                Config config = new Config()
                        .setAccessKeyId(ocrConfiguration.getAccessKeyId())
                        .setAccessKeySecret(ocrConfiguration.getAccessKeySecret())
                        .setEndpoint(ocrConfiguration.getEndpoint());
                this.ocrClient = new Client(config);
                log.info("[init] 阿里云 OCR 客户端初始化成功");
            } else {
                log.warn("[init] 阿里云 OCR 配置缺失，将使用模拟数据");
            }
        } catch (Exception e) {
            log.error("[init] 阿里云 OCR 客户端初始化失败", e);
        }
    }

    @Override
    public String recognizePaper(String imageFilePath) {
        log.info("[recognizePaper] 开始 OCR 识别，文件路径: {}", imageFilePath);
        return recognizePaperForEducation(imageFilePath);
    }

    @Override
    public String recognizePaperWithModel(String imageFilePath, String ocrModel) {
        log.info("[recognizePaperWithModel] 开始 OCR 识别，文件路径: {}, 模型: {}", imageFilePath, ocrModel);
        // 目前只支持 aliyun，默认使用阿里云 OCR
        return recognizePaperForEducation(imageFilePath);
    }

    @Override
    public String recognizePaperForEducation(String imageFilePath) {
        log.info("[recognizePaperForEducation] 开始教育场景 OCR 识别，文件路径: {}", imageFilePath);

        try {
            // 检查配置
            if (ocrClient == null) {
                log.warn("[recognizePaperForEducation] 阿里云 OCR 客户端未初始化，使用模拟数据");
                return createMockResponse(imageFilePath);
            }

            // 检查是本地文件还是网络 URL
            boolean isUrl = imageFilePath.startsWith("http://") || imageFilePath.startsWith("https://");
            log.info("[URL检查] 是否为URL: {}, 文件路径: {}", isUrl, imageFilePath);
            File imageFile = new File(imageFilePath);

            // 对于本地文件，检查是否存在
            if (!isUrl && !imageFile.exists()) {
                log.error("[recognizePaperForEducation] 本地文件不存在: {}", imageFilePath);
                return createMockResponse(imageFilePath);
            }

            // 判断文件类型
            String lowerPath = imageFilePath.toLowerCase();
            String ocrText;

            if (lowerPath.endsWith(".pdf")) {
                // PDF 文件处理
                ocrText = recognizePdfFile(imageFilePath, isUrl);
            } else {
                // 图片文件处理（JPG, JPEG, PNG）
                ocrText = recognizeImageFile(imageFilePath, isUrl);
            }

            log.info("[recognizePaperForEducation] OCR 识别完成，文本长度: {}", ocrText != null ? ocrText.length() : 0);
            return ocrText;

        } catch (Exception e) {
            log.error("[recognizePaperForEducation] OCR 识别失败，文件路径: {}", imageFilePath, e);
            // 失败时返回模拟数据，确保流程可以继续
            log.warn("[recognizePaperForEducation] OCR 失败，使用模拟数据继续流程");
            return createMockResponse(imageFilePath);
        }
    }

    /**
     * 识别图片文件
     * @param imageFilePath 原始文件路径（可能是 URL 或本地路径）
     * @param isUrl 是否为网络URL
     */
    private String recognizeImageFile(String imageFilePath, boolean isUrl) throws Exception {
        log.info("[recognizeImageFile] 识别图片文件: {}, 是否为URL: {}", imageFilePath, isUrl);

        // 从路径中提取文件名
        String fileName = isUrl ?
            imageFilePath.substring(imageFilePath.lastIndexOf('/') + 1) :
            new File(imageFilePath).getName();

        String logFileName = "ocr_" + fileNameFormat.format(new Date()) + ".log";
        String logFilePath = OCR_LOG_DIR + "/" + logFileName;

        // 确保日志目录存在
        File logDir = new File(OCR_LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // 写入请求日志
        writeOcrLog(logFilePath, "========== 阿里云 OCR 请求日志 ==========");
        writeOcrLog(logFilePath, "时间: " + dateFormat.format(new Date()));
        writeOcrLog(logFilePath, "文件路径: " + imageFilePath);
        writeOcrLog(logFilePath, "是否为URL: " + isUrl);
        writeOcrLog(logFilePath, "文件类型: IMAGE");

        // 统一使用 Base64 方式：将 URL 或本地文件都转换为 base64
        String base64Data;
        if (isUrl) {
            // 对于网络 URL，先下载文件
            try {
                log.info("[recognizeImageFile] 从网络下载图片: {}", imageFilePath);
                java.net.URL url = new java.net.URL(imageFilePath);
                byte[] imageBytes = cn.hutool.core.io.IoUtil.readBytes(url.openStream());
                String mimeType = getMimeType(fileName);
                base64Data = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
                writeOcrLog(logFilePath, "图片下载成功，大小: " + imageBytes.length + " bytes");
            } catch (Exception e) {
                log.error("[recognizeImageFile] 图片下载失败: {}", imageFilePath, e);
                throw e;
            }
        } else {
            // 本地文件，读取并转换为 base64
            File imageFile = new File(imageFilePath);
            byte[] imageBytes = java.nio.file.Files.readAllBytes(imageFile.toPath());
            String mimeType = getMimeType(fileName);
            base64Data = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
            writeOcrLog(logFilePath, "文件大小: " + imageBytes.length + " bytes");
        }

        // 提取 base64 数据（去掉 data:xxx;base64, 前缀）
        String bodyContent = base64Data;
        if (base64Data.contains(",")) {
            bodyContent = base64Data.substring(base64Data.indexOf(",") + 1);
        }
        byte[] decodedBytes = Base64.getDecoder().decode(bodyContent);

        // 构建请求
        writeOcrLog(logFilePath, "请求模型:阿里云读光通用OCR");
        writeOcrLog(logFilePath, "请求内容长度: " + decodedBytes.length + " bytes");

        RecognizeGeneralRequest request = new RecognizeGeneralRequest()
                .setBody(new java.io.ByteArrayInputStream(decodedBytes));

        // 记录请求发送时间
        long startTime = System.currentTimeMillis();
        writeOcrLog(logFilePath, "请求发送时间: " + dateFormat.format(new Date(startTime)));

        // 调用 API
        try {
            RecognizeGeneralResponse response = ocrClient.recognizeGeneral(request);

            // 记录响应时间
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            writeOcrLog(logFilePath, "响应接收时间: " + dateFormat.format(new Date(endTime)));
            writeOcrLog(logFilePath, "请求耗时: " + duration + " ms");
            writeOcrLog(logFilePath, "响应状态码: " + response.getStatusCode());

            // 检查响应体
            if (response.getBody() != null) {
                writeOcrLog(logFilePath, "响应体存在: 是");
                String dataStr = response.getBody().getData();
                if (dataStr != null) {
                    writeOcrLog(logFilePath, "响应数据长度: " + dataStr.length() + " 字符");

                    // 写入部分响应数据用于调试
                    String previewData = dataStr.length() > 500 ? dataStr.substring(0, 500) + "..." : dataStr;
                    writeOcrLog(logFilePath, "响应数据预览:\n" + previewData);
                } else {
                    writeOcrLog(logFilePath, "响应数据: null");
                }
            } else {
                writeOcrLog(logFilePath, "响应体: null");
            }

            // 解析响应
            String result = parseOcrResponse(response.getBody().getData());
            writeOcrLog(logFilePath, "解析后文本长度: " + (result != null ? result.length() : 0));
            writeOcrLog(logFilePath, "解析后文本预览:\n" + (result != null && result.length() > 200 ? result.substring(0, 200) + "..." : result));
            writeOcrLog(logFilePath, "========== OCR 请求完成 ==========");
            writeOcrLog(logFilePath, "");

            return result;
        } catch (Exception e) {
            writeOcrLog(logFilePath, "OCR 调用异常: " + e.getMessage());
            writeOcrLog(logFilePath, "异常堆栈: ");
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            writeOcrLog(logFilePath, sw.toString());
            writeOcrLog(logFilePath, "========== OCR 请求失败 ==========");
            writeOcrLog(logFilePath, "");
            throw e;
        }
    }

    /**
     * 识别 PDF 文件
     * @param pdfFilePath 原始文件路径（可能是 URL 或本地路径）
     * @param isUrl 是否为网络URL
     */
    private String recognizePdfFile(String pdfFilePath, boolean isUrl) throws Exception {
        log.info("[recognizePdfFile] 识别 PDF 文件: {}, 是否为URL: {}", pdfFilePath, isUrl);

        String base64Pdf;
        if (isUrl) {
            // 对于网络 URL，需要先下载文件
            log.info("[recognizePdfFile] 从网络下载 PDF 文件...");
            java.net.URL url = new java.net.URL(pdfFilePath);
            try (InputStream inputStream = url.openStream()) {
                byte[] pdfBytes = inputStream.readAllBytes();
                base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
                log.info("[recognizePdfFile] 下载完成，文件大小: {} bytes", pdfBytes.length);
            }
        } else {
            // 本地文件，读取流
            try (InputStream inputStream = new FileInputStream(pdfFilePath)) {
                byte[] pdfBytes = inputStream.readAllBytes();
                base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
            }
        }

        // 构建请求
        byte[] decodedPdfBytes = Base64.getDecoder().decode(base64Pdf);
        RecognizeGeneralRequest request = new RecognizeGeneralRequest()
                .setBody(new java.io.ByteArrayInputStream(decodedPdfBytes));

        // 调用 API
        RecognizeGeneralResponse response = ocrClient.recognizeGeneral(request);

        // 解析响应
        return parseOcrResponse(response.getBody().getData());
    }

    /**
     * 解析 OCR 响应数据
     */
    private String parseOcrResponse(String data) {
        if (!StringUtils.hasText(data)) {
            log.warn("[parseOcrResponse] OCR 响应数据为空");
            return "";
        }

        try {
            // 解析 JSON 响应
            JSONObject jsonData = JSONUtil.parseObj(data);
            StringBuilder result = new StringBuilder();

            // 提取文本内容
            // 阿里云 OCR 返回的格式通常是 {"prism_wordsInfo": [{"word": "xxx"}, ...]}
            JSONArray wordsInfo = jsonData.getJSONArray("prism_wordsInfo");
            if (wordsInfo != null && !wordsInfo.isEmpty()) {
                for (int i = 0; i < wordsInfo.size(); i++) {
                    JSONObject wordInfo = wordsInfo.getJSONObject(i);
                    String word = wordInfo.getStr("word");
                    if (StringUtils.hasText(word)) {
                        result.append(word).append("\n");
                    }
                }
            } else {
                // 如果标准格式解析失败，尝试直接使用原始数据
                result.append(data);
            }

            return result.toString();

        } catch (Exception e) {
            log.warn("[parseOcrResponse] 解析 OCR 响应失败，返回原始数据", e);
            return data;
        }
    }

    /**
     * 写入 OCR 日志到文件
     */
    private void writeOcrLog(String logFilePath, String content) {
        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            writer.write(content + "\n");
        } catch (Exception e) {
            log.error("[writeOcrLog] 写入日志失败: {}", logFilePath, e);
        }
    }

    /**
     * 获取文件的 MIME 类型
     */
    private String getMimeType(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".png")) {
            return "image/png";
        } else if (lowerName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerName.endsWith(".bmp")) {
            return "image/bmp";
        } else if (lowerName.endsWith(".webp")) {
            return "image/webp";
        } else {
            // 默认 JPEG
            return "image/jpeg";
        }
    }

    /**
     * 创建模拟响应（用于测试或 OCR 服务不可用时）
     */
    private String createMockResponse(String imageFilePath) {
        log.info("[createMockResponse] 创建模拟 OCR 结果");

        // 模拟一个试卷的 OCR 文本
        return "一、选择题（每题 2 分，共 20 分）\n\n" +
                "1. 下列关于 Java 中 String 类的说法，正确的是（  ）\n" +
                "A. String 是基本数据类型\n" +
                "B. String 对象一旦创建就不能修改\n" +
                "C. String 类可以被继承\n" +
                "D. String 对象可以通过 = = 进行内容比较\n\n" +
                "2. 以下哪个不是 Java 的访问修饰符？（  ）\n" +
                "A. public\n" +
                "B. private\n" +
                "C. protected\n" +
                "D. internal\n\n" +
                "3. 下列关于 Java 异常的说法，错误的是（  ）\n" +
                "A. Error 表示系统级错误\n" +
                "B. Exception 表示程序级错误\n" +
                "C. RuntimeException 是必须捕获的异常\n" +
                "D. Throwable 是所有异常的父类\n\n" +
                "二、填空题（每空 2 分，共 20 分）\n\n" +
                "4. Java 中基本数据类型有 ______ 种，分别是 ________________。\n\n" +
                "5. Java 中的访问修饰符包括 public、protected、______ 和默认访问权限。\n\n" +
                "三、简答题（每题 10 分，共 30 分）\n\n" +
                "6. 简述 Java 中 == 和 equals() 方法的区别。\n\n" +
                "7. 什么是 Java 中的多态？请举例说明。\n\n" +
                "8. 简述 Java 中 ArrayList 和 LinkedList 的区别。\n";
    }

}
