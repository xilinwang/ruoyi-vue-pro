package cn.iocoder.yudao.module.studybuddy.service.ocr;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.studybuddy.framework.ocr.config.IflowOcrConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * iFlow OCR 服务实现
 *
 * 使用通义千问 VL 模型 (qwen3-vl-plus) 进行试卷识别
 *
 * @author StudyBuddy
 */
@Service("iflowOcrService")
@Slf4j
public class IflowOcrServiceImpl implements OcrService {

    @Resource
    private IflowOcrConfiguration iflowOcrConfiguration;

    @Override
    public String recognizePaper(String imageFilePath) {
        return recognizePaperWithModel(imageFilePath, "iflow");
    }

    @Override
    public String recognizePaperForEducation(String imageFilePath) {
        return recognizePaperWithModel(imageFilePath, "iflow");
    }

    @Override
    public String recognizePaperWithModel(String imageFilePath, String ocrModel) {
        return recognizePaperWithModelAndSubject(imageFilePath, ocrModel, null);
    }

    @Override
    public String recognizePaperWithModelAndSubject(String imageFilePath, String ocrModel, String subject) {
        log.info("[recognizePaperWithModelAndSubject] 开始 iFlow OCR 识别，文件路径: {}, 模型: {}, 科目: {}",
                imageFilePath, ocrModel, subject);

        try {
            // 检查配置
            if (!StringUtils.hasText(iflowOcrConfiguration.getApiKey())) {
                log.error("[recognizePaperWithModelAndSubject] iFlow OCR API Key 未配置，OCR服务不可用");
                throw new RuntimeException("iFlow OCR API Key 未配置，OCR服务不可用");
            }

            // 检查文件是否存在
            File imageFile = new File(imageFilePath);
            if (!imageFile.exists()) {
                log.error("[recognizePaperWithModelAndSubject] 文件不存在: {}", imageFilePath);
                throw new RuntimeException("文件不存在: " + imageFilePath);
            }

            // 读取图片文件并转换为 Base64
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 根据文件类型确定 MIME 类型
            String mimeType = getMimeType(imageFile.getName());
            String imageDataUrl = "data:" + mimeType + ";base64," + base64Image;

            // 调用 iFlow API
            String ocrText = callIflowApi(imageDataUrl, imageFile.getName(), subject);

            log.info("[recognizePaperWithModelAndSubject] iFlow OCR 识别完成，文本长度: {}",
                    ocrText != null ? ocrText.length() : 0);
            return ocrText;

        } catch (Exception e) {
            log.error("[recognizePaperWithModelAndSubject] iFlow OCR 识别失败，文件路径: {}", imageFilePath, e);
            // 直接抛出异常，不再使用模拟数据
            throw new RuntimeException("iFlow OCR 识别失败: " + e.getMessage(), e);
        }
    }

    /**
     * 调用 iFlow API (OpenAI 兼容格式)
     */
    private String callIflowApi(String imageDataUrl, String fileName, String subject) throws Exception {
        String apiUrl = iflowOcrConfiguration.getBaseUrl() + "/chat/completions";
        String model = iflowOcrConfiguration.getModel();

        log.info("[callIflowApi] 调用 iFlow API，URL: {}, Model: {}, Subject: {}", apiUrl, model, subject);

        // 构建请求体 (OpenAI Vision API 格式)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", createVisionMessages(imageDataUrl, subject));
        requestBody.put("max_tokens", 4096);

        String jsonBody = JSONUtil.toJsonStr(requestBody);

        // 发送 HTTP 请求
        HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + iflowOcrConfiguration.getApiKey())
                .body(jsonBody)
                .timeout(60000) // 60秒超时
                .execute();

        if (!response.isOk()) {
            log.error("[callIflowApi] iFlow API 调用失败，状态码: {}, 响应: {}", response.getStatus(), response.body());
            throw new RuntimeException("iFlow API 调用失败: " + response.getStatus());
        }

        // 解析响应
        JSONObject responseJson = JSONUtil.parseObj(response.body());
        JSONObject choice = responseJson.getJSONArray("choices").getJSONObject(0);
        JSONObject message = choice.getJSONObject("message");
        String content = message.getStr("content");

        log.info("[callIflowApi] iFlow API 调用成功，返回内容长度: {}", content != null ? content.length() : 0);

        return content;
    }

    /**
     * 创建视觉识别请求消息
     */
    private List<Map<String, Object>> createVisionMessages(String imageDataUrl, String subject) {
        List<Map<String, Object>> messages = new ArrayList<>();

        // 构建系统消息，包含科目信息
        String systemPrompt = "你是一个专业的教育试卷识别助手。请仔细识别图片中的试卷内容，包括所有题目、选项、答案区域等。";
        if (subject != null && !subject.isEmpty()) {
            systemPrompt += "这是一张" + subject + "试卷，请注意识别相关的专业术语和格式。";
        }
        systemPrompt += "请按照原始格式输出识别的文字内容，保持题目编号、选项格式不变。如果有手写内容，也请尽量识别。";

        // 系统消息
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);

        // 用户消息（包含图片）
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");

        List<Map<String, Object>> contentList = new ArrayList<>();

        // 文本内容
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", "请识别这张试卷图片中的所有文字内容，包括题目、选项、答案等。请保持原始格式输出，不要添加额外解释。");
        contentList.add(textContent);

        // 图片内容
        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        Map<String, String> imageUrl = new HashMap<>();
        imageUrl.put("url", imageDataUrl);
        imageContent.put("image_url", imageUrl);
        contentList.add(imageContent);

        userMessage.put("content", contentList);
        messages.add(userMessage);

        return messages;
    }

    /**
     * 根据文件名获取 MIME 类型
     */
    private String getMimeType(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".png")) {
            return "image/png";
        } else if (lowerName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerName.endsWith(".webp")) {
            return "image/webp";
        } else {
            // 默认使用 JPEG
            return "image/jpeg";
        }
    }

}
