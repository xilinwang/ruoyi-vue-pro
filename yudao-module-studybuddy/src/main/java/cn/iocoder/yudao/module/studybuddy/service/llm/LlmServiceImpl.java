package cn.iocoder.yudao.module.studybuddy.service.llm;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.studybuddy.framework.llm.config.LlmConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek LLM 服务实现
 *
 * 使用 DeepSeek API 进行题目结构解析和答案分析
 *
 * @author StudyBuddy
 */
@Service
@Slf4j
public class LlmServiceImpl implements LlmService {

    @Resource
    private LlmConfiguration llmConfiguration;

    /**
     * 题目结构解析 Prompt
     */
    private static final String PARSE_QUESTION_PROMPT =
        "你是一个专业的教育辅助AI助手。请分析以下试卷OCR识别的文本内容，提取出所有题目及其相关信息。\n\n" +
        "要求：\n" +
        "1. 识别题目类型（选择题、填空题、简答题、计算题等）\n" +
        "2. 提取题目编号\n" +
        "3. 提取题目内容\n" +
        "4. 提取每个选项（对于选择题）\n" +
        "5. 识别并提取标准答案（如果试卷中包含答案）\n" +
        "6. 识别知识点标签\n\n" +
        "请以JSON格式返回，格式如下：\n" +
        "{\n" +
        "  \"questions\": [\n" +
        "    {\n" +
        "      \"questionNo\": \"1\",\n" +
        "      \"type\": \"选择题\",\n" +
        "      \"content\": \"题目内容\",\n" +
        "      \"options\": [\"A. 选项1\", \"B. 选项2\", \"C. 选项3\", \"D. 选项4\"],\n" +
        "      \"standardAnswer\": \"B\",\n" +
        "      \"knowledgePoints\": [\"知识点1\", \"知识点2\"]\n" +
        "    }\n" +
        "  ]\n" +
        "}\n\n" +
        "注意事项：\n" +
        "- 如果OCR文本中包含标准答案，请提取并放入standardAnswer字段\n" +
        "- 对于选择题，标准答案通常是选项字母（如A、B、C、D）\n" +
        "- 对于填空题和简答题，标准答案是具体的文字答案\n" +
        "- 如果无法识别标准答案，standardAnswer字段设为null或空字符串\n\n" +
        "OCR文本如下：\n";

    /**
     * 答案分析 Prompt
     */
    private static final String ANALYZE_ANSWER_PROMPT_TEMPLATE =
        "你是一个专业的教育辅助AI助手。请分析学生的答案，判断是否正确，并给出详细反馈。\n\n" +
        "题目内容：%s\n" +
        "标准答案：%s\n" +
        "学生答案：%s\n\n" +
        "要求：\n" +
        "1. 判断答案是否正确（is_correct: true/false）\n" +
        "2. 如果错误，分析错误原因（error_analysis）\n" +
        "3. 提供更好的解题思路或标准答案解析（better_solution）\n\n" +
        "请以JSON格式返回，格式如下：\n" +
        "{\n" +
        "  \"is_correct\": true,\n" +
        "  \"error_analysis\": \"错误分析\",\n" +
        "  \"better_solution\": \"更好的解题思路\"\n" +
        "}";

    @Override
    public String parseQuestionStructure(String ocrText) {
        log.info("[parseQuestionStructure] 开始解析题目结构，OCR文本长度: {}, 使用模型: {}", 
                ocrText != null ? ocrText.length() : 0, 
                llmConfiguration.getProvider() + "/" + llmConfiguration.getModel());

        try {
            // 检查配置
            if (!StringUtils.hasText(llmConfiguration.getApiKey())) {
                log.warn("[parseQuestionStructure] LLM API Key 未配置，使用模拟数据");
                return createMockQuestionJson(ocrText);
            }

            // 构建请求
            String prompt = PARSE_QUESTION_PROMPT + "\n" + ocrText;

            // 调用 LLM API
            String response = callLlmApi(prompt);

            log.info("[parseQuestionStructure] 题目结构解析完成");
            return response;

        } catch (Exception e) {
            log.error("[parseQuestionStructure] 题目结构解析失败", e);
            // 失败时返回模拟数据
            log.warn("[parseQuestionStructure] 使用模拟数据继续流程");
            return createMockQuestionJson(ocrText);
        }
    }

    @Override
    public String analyzeAnswer(String questionContent, String studentAnswer, String standardAnswer) {
        log.info("[analyzeAnswer] 开始分析答案，使用模型: {}", 
                llmConfiguration.getProvider() + "/" + llmConfiguration.getModel());

        try {
            // 检查配置
            if (!StringUtils.hasText(llmConfiguration.getApiKey())) {
                log.warn("[analyzeAnswer] LLM API Key 未配置，使用模拟数据");
                return createMockAnalysisResult();
            }

            // 构建请求
            String prompt = String.format(ANALYZE_ANSWER_PROMPT_TEMPLATE, questionContent, standardAnswer, studentAnswer);

            // 调用 LLM API
            String response = callLlmApi(prompt);

            log.info("[analyzeAnswer] 答案分析完成");
            return response;

        } catch (Exception e) {
            log.error("[analyzeAnswer] 答案分析失败", e);
            // 失败时返回模拟数据
            log.warn("[analyzeAnswer] 使用模拟数据继续流程");
            return createMockAnalysisResult();
        }
    }

    /**
     * 调用 LLM API（支持 DeepSeek 和 Qwen）
     */
    private String callLlmApi(String prompt) throws Exception {
        String url = llmConfiguration.getBaseUrl() + "/chat/completions";

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", llmConfiguration.getModel());

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        requestBody.put("messages", messages);

        // 设置温度和最大token数
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 4000);

        log.info("[callLlmApi] 调用 LLM API，Provider: {}, Model: {}, URL: {}", 
                llmConfiguration.getProvider(), llmConfiguration.getModel(), url);

        // 发送请求
        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + llmConfiguration.getApiKey())
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(requestBody))
                .timeout(llmConfiguration.getTimeout() * 1000)
                .execute();

        // 检查响应状态
        if (!response.isOk()) {
            throw new RuntimeException("LLM API 调用失败，状态码: " + response.getStatus() + 
                    ", 响应: " + response.body());
        }

        // 解析响应
        String responseBody = response.body();
        JSONObject jsonResponse = JSONUtil.parseObj(responseBody);

        // 提取内容
        String content = jsonResponse.getByPath("choices[0].message.content", String.class);

        if (content == null) {
            log.error("[callLlmApi] API 响应格式错误: {}", responseBody);
            throw new RuntimeException("LLM API 响应格式错误");
        }

        return content;
    }

    /**
     * 创建模拟题目 JSON（用于测试或 API 不可用时）
     */
    private String createMockQuestionJson(String ocrText) {
        log.info("[createMockQuestionJson] 创建模拟题目数据");

        JSONObject result = new JSONObject();
        List<JSONObject> questions = new ArrayList<>();

        // 模拟题目1
        JSONObject q1 = new JSONObject();
        q1.set("questionNo", "1");
        q1.set("type", "选择题");
        q1.set("content", "下列关于 Java 中 String 类的说法，正确的是（）");
        q1.set("options", List.of(
            "A. String 是基本数据类型",
            "B. String 对象一旦创建就不能修改",
            "C. String 类可以被继承",
            "D. String 对象可以通过 == 进行内容比较"
        ));
        q1.set("standardAnswer", "B");
        q1.set("knowledgePoints", List.of("String类", "基础语法"));
        questions.add(q1);

        // 模拟题目2
        JSONObject q2 = new JSONObject();
        q2.set("questionNo", "2");
        q2.set("type", "选择题");
        q2.set("content", "以下哪个不是 Java 的访问修饰符？（）");
        q2.set("options", List.of(
            "A. public",
            "B. private",
            "C. protected",
            "D. internal"
        ));
        q2.set("standardAnswer", "D");
        q2.set("knowledgePoints", List.of("访问控制", "基础语法"));
        questions.add(q2);

        // 模拟题目3
        JSONObject q3 = new JSONObject();
        q3.set("questionNo", "6");
        q3.set("type", "简答题");
        q3.set("content", "简述 Java 中 == 和 equals() 方法的区别。");
        q3.set("options", new ArrayList<>());
        q3.set("standardAnswer", "== 比较的是两个对象的引用地址是否相同，而 equals() 方法比较的是两个对象的内容是否相同。对于 String 对象，建议使用 equals() 方法进行内容比较。");
        q3.set("knowledgePoints", List.of("字符串比较", "Object类"));
        questions.add(q3);

        result.set("questions", questions);

        return JSONUtil.toJsonStr(result);
    }

    /**
     * 创建模拟分析结果（用于测试或 API 不可用时）
     */
    private String createMockAnalysisResult() {
        log.info("[createMockAnalysisResult] 创建模拟分析结果");

        JSONObject result = new JSONObject();
        result.set("is_correct", false);
        result.set("error_analysis", "学生答案不完整，只说明了 equals() 方法用于内容比较，但没有解释 == 比较的是引用。");
        result.set("better_solution", "== 比较的是两个对象的引用地址是否相同，而 equals() 方法比较的是两个对象的内容是否相同。对于 String 对象，建议使用 equals() 方法进行内容比较。");

        return JSONUtil.toJsonStr(result);
    }

}
