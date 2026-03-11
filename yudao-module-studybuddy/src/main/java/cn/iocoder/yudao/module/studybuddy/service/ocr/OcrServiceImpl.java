package cn.iocoder.yudao.module.studybuddy.service.ocr;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.studybuddy.framework.ocr.config.OcrConfiguration;
import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeEduPaperOcrRequest;
import com.aliyun.ocr_api20210707.models.RecognizeEduPaperOcrResponse;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * 阿里云 OCR 服务实现
 *
 * 使用阿里云读光 OCR API 进行试卷识别
 * API文档: https://help.aliyun.com/zh/ocr/developer-reference/api-ocr-api-2021-07-07-recognizeedupaperocr
 *
 * @author StudyBuddy
 */
@Service
@Slf4j
public class OcrServiceImpl implements OcrService {

    // OCR 日志目录 - 使用绝对路径，测试时使用 ./temp
    private static final String OCR_LOG_DIR = "./temp/ocr";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");

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
        log.info("[OcrServiceInit] 开始初始化阿里云 OCR 客户端...");
        log.info("[OcrServiceInit] 配置信息: accessKeyId={}, endpoint={}",
                StringUtils.hasText(ocrConfiguration.getAccessKeyId()) ? "已设置" : "未设置",
                ocrConfiguration.getEndpoint());

        try {
            String accessKeyId = ocrConfiguration.getAccessKeyId();
            String accessKeySecret = ocrConfiguration.getAccessKeySecret();

            if (StringUtils.hasText(accessKeyId) && StringUtils.hasText(accessKeySecret)) {
                Config config = new Config()
                        .setAccessKeyId(accessKeyId)
                        .setAccessKeySecret(accessKeySecret)
                        .setEndpoint(ocrConfiguration.getEndpoint());
                this.ocrClient = new Client(config);
                log.info("[OcrServiceInit] 阿里云 OCR 客户端初始化成功");
                log.info("[OcrServiceInit] AccessKeyId: {}", accessKeyId.substring(0, Math.min(4, accessKeyId.length())) + "****");
            } else {
                log.error("[OcrServiceInit] 阿里云 OCR 配置缺失 (accessKeyId={}, accessKeySecret={})，OCR服务不可用",
                        StringUtils.hasText(accessKeyId) ? "已设置" : "未设置",
                        StringUtils.hasText(accessKeySecret) ? "已设置" : "未设置");
            }
        } catch (Exception e) {
            log.error("[OcrServiceInit] 阿里云 OCR 客户端初始化失败", e);
        }
    }

    @Override
    public String recognizePaper(String imageFilePath) {
        log.info("[recognizePaper] 开始 OCR 识别，文件路径: {}", imageFilePath);
        return recognizePaperForEducation(imageFilePath, null);
    }

    @Override
    public String recognizePaperWithModel(String imageFilePath, String ocrModel) {
        log.info("[recognizePaperWithModel] 开始 OCR 识别，文件路径: {}, 模型: {}", imageFilePath, ocrModel);
        // 默认使用阿里云 OCR，不传递科目
        return recognizePaperForEducation(imageFilePath, null);
    }

    @Override
    public String recognizePaperWithModelAndSubject(String imageFilePath, String ocrModel, String subject) {
        log.info("[recognizePaperWithModelAndSubject] 开始 OCR 识别，文件路径: {}, 模型: {}, 科目: {}",
                imageFilePath, ocrModel, subject);
        return recognizePaperForEducation(imageFilePath, subject);
    }

    @Override
    public String recognizePaperForEducation(String imageFilePath) {
        return recognizePaperForEducation(imageFilePath, null);
    }

    /**
     * 识别教育场景试卷
     *
     * @param imageFilePath 图片文件路径
     * @param subject 科目（可选，用于提高识别准确率）
     * @return OCR 识别结果
     */
    private String recognizePaperForEducation(String imageFilePath, String subject) {
        log.info("[recognizePaperForEducation] ========== 开始教育场景 OCR 识别 ==========");
        log.info("[recognizePaperForEducation] 文件路径: {}, 科目: {}", imageFilePath, subject);

        String logFileName = "ocr_" + fileNameFormat.format(new Date()) + ".log";
        String logFilePath = OCR_LOG_DIR + "/" + logFileName;

        // 确保日志目录存在
        File logDir = new File(OCR_LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
            log.info("[recognizePaperForEducation] 创建OCR日志目录: {}", OCR_LOG_DIR);
        }

        // 写入请求日志
        writeOcrLog(logFilePath, "========== 阿里云 OCR 请求日志 ==========");
        writeOcrLog(logFilePath, "时间: " + dateFormat.format(new Date()));
        writeOcrLog(logFilePath, "文件路径: " + imageFilePath);
        writeOcrLog(logFilePath, "科目: " + (subject != null ? subject : "未指定"));

        try {
            // 检查配置 - 如果没有配置则直接抛出异常
            if (ocrClient == null) {
                log.error("[recognizePaperForEducation] 阿里云 OCR 客户端未初始化，OCR服务不可用");
                writeOcrLog(logFilePath, "错误: OCR 客户端未初始化，OCR服务不可用");
                throw new RuntimeException("阿里云 OCR 客户端未初始化，OCR服务不可用");
            }

            // 检查是本地文件还是网络 URL
            boolean isUrl = imageFilePath.startsWith("http://") || imageFilePath.startsWith("https://");
            log.info("[recognizePaperForEducation] URL检查: 是否为URL: {}", isUrl);

            File imageFile = new File(imageFilePath);

            // 对于本地文件，检查是否存在
            if (!isUrl && !imageFile.exists()) {
                log.error("[recognizePaperForEducation] 本地文件不存在: {}", imageFilePath);
                writeOcrLog(logFilePath, "错误: 本地文件不存在: " + imageFilePath);
                throw new RuntimeException("文件不存在: " + imageFilePath);
            }

            // 判断文件类型
            String lowerPath = imageFilePath.toLowerCase();
            String ocrText;

            if (lowerPath.endsWith(".pdf")) {
                // PDF 文件处理
                ocrText = recognizePdfFile(imageFilePath, isUrl, logFilePath, subject);
            } else {
                // 图片文件处理（JPG, JPEG, PNG）
                ocrText = recognizeImageFile(imageFilePath, isUrl, logFilePath, subject);
            }

            log.info("[recognizePaperForEducation] OCR 识别完成，文本长度: {}", ocrText != null ? ocrText.length() : 0);
            writeOcrLog(logFilePath, "OCR 识别完成，文本长度: " + (ocrText != null ? ocrText.length() : 0));
            writeOcrLog(logFilePath, "========== OCR 请求完成 ==========");

            return ocrText;

        } catch (Exception e) {
            log.error("[recognizePaperForEducation] OCR 识别失败，文件路径: {}", imageFilePath, e);
            writeOcrLog(logFilePath, "OCR 识别异常: " + e.getMessage());
            writeOcrLog(logFilePath, "异常堆栈: ");
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            writeOcrLog(logFilePath, sw.toString());
            writeOcrLog(logFilePath, "========== OCR 请求失败 ==========");

            // 直接抛出异常，不再使用模拟数据
            log.error("[recognizePaperForEducation] OCR 识别失败，抛出异常");
            throw new RuntimeException("OCR 识别失败: " + e.getMessage(), e);
        }
    }

    /**
     * 识别图片文件
     *
     * @param imageFilePath 原始文件路径（可能是 URL 或本地路径）
     * @param isUrl 是否为网络URL
     * @param logFilePath 日志文件路径
     * @param subject 科目
     */
    private String recognizeImageFile(String imageFilePath, boolean isUrl, String logFilePath, String subject) throws Exception {
        log.info("[recognizeImageFile] 识别图片文件: {}, 是否为URL: {}, 科目: {}", imageFilePath, isUrl, subject);
        writeOcrLog(logFilePath, "文件类型: IMAGE");
        writeOcrLog(logFilePath, "是否为URL: " + isUrl);

        // 从路径中提取文件名
        String fileName = isUrl ?
            imageFilePath.substring(imageFilePath.lastIndexOf('/') + 1) :
            new File(imageFilePath).getName();

        writeOcrLog(logFilePath, "文件名: " + fileName);

        // 统一使用 Base64 方式：将 URL 或本地文件都转换为 base64
        String base64Data;
        if (isUrl) {
            // 对于网络 URL，先下载文件
            try {
                log.info("[recognizeImageFile] 从网络下载图片: {}", imageFilePath);
                writeOcrLog(logFilePath, "开始下载网络图片...");
                java.net.URL url = new java.net.URL(imageFilePath);
                byte[] imageBytes = cn.hutool.core.io.IoUtil.readBytes(url.openStream());
                String mimeType = getMimeType(fileName);
                base64Data = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
                writeOcrLog(logFilePath, "图片下载成功，大小: " + imageBytes.length + " bytes");
                log.info("[recognizeImageFile] 图片下载成功，大小: {} bytes", imageBytes.length);
            } catch (Exception e) {
                log.error("[recognizeImageFile] 图片下载失败: {}", imageFilePath, e);
                writeOcrLog(logFilePath, "图片下载失败: " + e.getMessage());
                throw e;
            }
        } else {
            // 本地文件，读取并转换为 base64
            File imageFile = new File(imageFilePath);
            byte[] imageBytes = java.nio.file.Files.readAllBytes(imageFile.toPath());
            String mimeType = getMimeType(fileName);
            base64Data = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
            writeOcrLog(logFilePath, "文件大小: " + imageBytes.length + " bytes");
            log.info("[recognizeImageFile] 文件大小: {} bytes", imageBytes.length);
        }

        // 提取 base64 数据（去掉 data:xxx;base64, 前缀）
        String bodyContent = base64Data;
        if (base64Data.contains(",")) {
            bodyContent = base64Data.substring(base64Data.indexOf(",") + 1);
        }
        byte[] decodedBytes = Base64.getDecoder().decode(bodyContent);

        // 构建请求 - 使用教育试卷OCR API
        writeOcrLog(logFilePath, "请求模型: 阿里云读光教育试卷OCR (RecognizeEduPaperOcr)");
        writeOcrLog(logFilePath, "请求内容长度: " + decodedBytes.length + " bytes");
        writeOcrLog(logFilePath, "ImageType: photo");
        writeOcrLog(logFilePath, "OutputOricoord: true");
        writeOcrLog(logFilePath, "Subject: " + (subject != null ? subject : "未指定"));

        // 创建请求，设置所有参数
        RecognizeEduPaperOcrRequest request = new RecognizeEduPaperOcrRequest()
                .setBody(new java.io.ByteArrayInputStream(decodedBytes))
                .setImageType("photo")
                .setOutputOricoord(true);

        // 如果提供了科目，设置科目参数
        if (StringUtils.hasText(subject)) {
            request.setSubject(subject);
            log.info("[recognizeImageFile] 设置科目: {}", subject);
        }

        // 记录请求发送时间
        long startTime = System.currentTimeMillis();
        writeOcrLog(logFilePath, "请求发送时间: " + dateFormat.format(new Date(startTime)));
        log.info("[recognizeImageFile] 开始调用 OCR API...");

        // 调用 API
        try {
            RecognizeEduPaperOcrResponse response = ocrClient.recognizeEduPaperOcr(request);

            // 记录响应时间
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            writeOcrLog(logFilePath, "响应接收时间: " + dateFormat.format(new Date(endTime)));
            writeOcrLog(logFilePath, "请求耗时: " + duration + " ms");
            writeOcrLog(logFilePath, "响应状态码: " + response.getStatusCode());

            log.info("[recognizeImageFile] OCR API 调用成功，耗时: {} ms", duration);

            // 检查响应体
            if (response.getBody() != null) {
                writeOcrLog(logFilePath, "响应体存在: 是");
                String dataStr = response.getBody().getData();
                if (dataStr != null && !dataStr.isEmpty()) {
                    writeOcrLog(logFilePath, "响应数据长度: " + dataStr.length() + " 字符");

                    // 写入完整响应数据用于调试
                    writeOcrLog(logFilePath, "========== 原始响应数据 ==========");
                    writeOcrLog(logFilePath, dataStr);
                    writeOcrLog(logFilePath, "========== 响应数据结束 ==========");

                    // 解析响应
                    String result = parseOcrResponse(dataStr);
                    writeOcrLog(logFilePath, "解析后文本长度: " + (result != null ? result.length() : 0));
                    writeOcrLog(logFilePath, "解析后文本预览:\n" + (result != null && result.length() > 500 ? result.substring(0, 500) + "..." : result));

                    return result;
                } else {
                    writeOcrLog(logFilePath, "响应数据为空");
                    log.warn("[recognizeImageFile] OCR 响应数据为空");
                    throw new RuntimeException("OCR 识别失败：返回数据为空");
                }
            } else {
                writeOcrLog(logFilePath, "响应体为 null");
                log.warn("[recognizeImageFile] OCR 响应体为 null");
                throw new RuntimeException("OCR 识别失败：响应体为空");
            }

        } catch (Exception e) {
            writeOcrLog(logFilePath, "OCR 调用异常: " + e.getMessage());
            writeOcrLog(logFilePath, "异常堆栈: ");
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            writeOcrLog(logFilePath, sw.toString());
            writeOcrLog(logFilePath, "========== OCR 请求失败 ==========");
            log.error("[recognizeImageFile] OCR 调用异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 识别 PDF 文件
     *
     * @param pdfFilePath 原始文件路径（可能是 URL 或本地路径）
     * @param isUrl 是否为网络URL
     * @param logFilePath 日志文件路径
     * @param subject 科目
     */
    private String recognizePdfFile(String pdfFilePath, boolean isUrl, String logFilePath, String subject) throws Exception {
        log.info("[recognizePdfFile] 识别 PDF 文件: {}, 是否为URL: {}, 科目: {}", pdfFilePath, isUrl, subject);
        writeOcrLog(logFilePath, "文件类型: PDF");
        writeOcrLog(logFilePath, "ImageType: photo");
        writeOcrLog(logFilePath, "OutputOricoord: true");
        writeOcrLog(logFilePath, "Subject: " + (subject != null ? subject : "未指定"));

        String base64Pdf;
        if (isUrl) {
            // 对于网络 URL，需要先下载文件
            log.info("[recognizePdfFile] 从网络下载 PDF 文件...");
            writeOcrLog(logFilePath, "开始下载网络PDF文件...");
            java.net.URL url = new java.net.URL(pdfFilePath);
            try (java.io.InputStream inputStream = url.openStream()) {
                byte[] pdfBytes = inputStream.readAllBytes();
                base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
                writeOcrLog(logFilePath, "PDF下载完成，文件大小: " + pdfBytes.length + " bytes");
                log.info("[recognizePdfFile] 下载完成，文件大小: {} bytes", pdfBytes.length);
            }
        } else {
            // 本地文件，读取流
            try (java.io.InputStream inputStream = new FileInputStream(pdfFilePath)) {
                byte[] pdfBytes = inputStream.readAllBytes();
                base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
                writeOcrLog(logFilePath, "PDF文件大小: " + pdfBytes.length + " bytes");
            }
        }

        // 构建请求 - 使用教育试卷OCR API
        byte[] decodedPdfBytes = Base64.getDecoder().decode(base64Pdf);
        writeOcrLog(logFilePath, "请求模型: 阿里云读光教育试卷OCR (RecognizeEduPaperOcr)");
        writeOcrLog(logFilePath, "请求内容长度: " + decodedPdfBytes.length + " bytes");

        // 创建请求，设置所有参数
        RecognizeEduPaperOcrRequest request = new RecognizeEduPaperOcrRequest()
                .setBody(new java.io.ByteArrayInputStream(decodedPdfBytes))
                .setImageType("photo")
                .setOutputOricoord(true);

        // 如果提供了科目，设置科目参数
        if (StringUtils.hasText(subject)) {
            request.setSubject(subject);
        }

        // 调用 API
        long startTime = System.currentTimeMillis();
        RecognizeEduPaperOcrResponse response = ocrClient.recognizeEduPaperOcr(request);
        long endTime = System.currentTimeMillis();

        writeOcrLog(logFilePath, "请求耗时: " + (endTime - startTime) + " ms");
        log.info("[recognizePdfFile] OCR API 调用成功，耗时: {} ms", endTime - startTime);

        // 解析响应
        if (response.getBody() != null && response.getBody().getData() != null && !response.getBody().getData().isEmpty()) {
            return parseOcrResponse(response.getBody().getData());
        }

        throw new RuntimeException("OCR 识别失败：PDF响应数据为空");
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

            // 阿里云教育试卷OCR返回的格式
            // 尝试多种可能的数据格式

            // 格式1: prism_wordsInfo (通用格式)
            JSONArray wordsInfo = jsonData.getJSONArray("prism_wordsInfo");
            if (wordsInfo != null && !wordsInfo.isEmpty()) {
                log.info("[parseOcrResponse] 使用 prism_wordsInfo 格式解析");
                for (int i = 0; i < wordsInfo.size(); i++) {
                    JSONObject wordInfo = wordsInfo.getJSONObject(i);
                    String word = wordInfo.getStr("word");
                    if (StringUtils.hasText(word)) {
                        result.append(word).append("\n");
                    }
                }
                if (result.length() > 0) {
                    return result.toString();
                }
            }

            // 格式2: data.content (教育试卷格式)
            JSONObject dataObj = jsonData.getJSONObject("data");
            if (dataObj != null) {
                String content = dataObj.getStr("content");
                if (StringUtils.hasText(content)) {
                    log.info("[parseOcrResponse] 使用 data.content 格式解析");
                    return content;
                }
                // 格式3: data 本身是数组
                JSONArray dataArray = dataObj.getJSONArray("data");
                if (dataArray != null && !dataArray.isEmpty()) {
                    log.info("[parseOcrResponse] 使用 data.data 数组格式解析");
                    for (int i = 0; i < dataArray.size(); i++) {
                        Object item = dataArray.get(i);
                        if (item instanceof JSONObject) {
                            String text = ((JSONObject) item).getStr("text");
                            if (StringUtils.hasText(text)) {
                                result.append(text).append("\n");
                            }
                        } else {
                            result.append(item).append("\n");
                        }
                    }
                    if (result.length() > 0) {
                        return result.toString();
                    }
                }
            }

            // 如果标准格式解析失败，尝试直接使用原始数据
            log.warn("[parseOcrResponse] 无法解析标准格式，使用原始数据");
            return data;

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

}