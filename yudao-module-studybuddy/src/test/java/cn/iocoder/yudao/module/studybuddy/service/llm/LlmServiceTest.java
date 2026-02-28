package cn.iocoder.yudao.module.studybuddy.service.llm;

import cn.iocoder.yudao.module.studybuddy.framework.llm.config.LlmConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LlmService 单元测试
 *
 * @author StudyBuddy
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceTest {

    @InjectMocks
    private LlmServiceImpl llmService;

    private LlmConfiguration llmConfiguration;

    @BeforeEach
    void setUp() {
        llmConfiguration = new LlmConfiguration();
        llmConfiguration.setApiKey("test-api-key");
        llmConfiguration.setBaseUrl("https://api.deepseek.com");
        llmConfiguration.setModel("deepseek-chat");
        llmConfiguration.setTimeout(30);
    }

    @Test
    void testParseQuestionStructure_WithOcrText() {
        // Arrange
        String ocrText = "一、选择题\n1. 下列关于 Java 中 String 类的说法，正确的是（）";

        // Act
        String result = llmService.parseQuestionStructure(ocrText);

        // Assert
        assertNotNull(result);
        // 应该返回 JSON 格式的题目数据
        assertTrue(result.contains("{") || result.contains("questions"));
    }

    @Test
    void testParseQuestionStructure_EmptyText() {
        // Act
        String result = llmService.parseQuestionStructure("");

        // Assert
        assertNotNull(result);
        // 应该返回模拟数据
        assertTrue(result.contains("questions"));
    }

    @Test
    void testParseQuestionStructure_NullText() {
        // Act
        String result = llmService.parseQuestionStructure(null);

        // Assert
        assertNotNull(result);
        // 应该返回模拟数据
        assertTrue(result.contains("questions"));
    }

    @Test
    void testAnalyzeAnswer_WithValidInput() {
        // Arrange
        String questionContent = "简述 Java 中 == 和 equals() 方法的区别。";
        String studentAnswer = "== 比较引用，equals 比较内容";
        String standardAnswer = "== 比较的是两个对象的引用地址是否相同，而 equals() 方法比较的是两个对象的内容是否相同。";

        // Act
        String result = llmService.analyzeAnswer(questionContent, studentAnswer, standardAnswer);

        // Assert
        assertNotNull(result);
        // 应该包含分析结果字段
        assertTrue(result.contains("is_correct") || result.contains("error_analysis"));
    }

    @Test
    void testAnalyzeAnswer_EmptyAnswers() {
        // Act
        String result = llmService.analyzeAnswer("", "", "");

        // Assert
        assertNotNull(result);
        // 应该返回模拟分析结果
        assertTrue(result.contains("{"));
    }

    @Test
    void testAnalyzeAnswer_NullInput() {
        // Act
        String result = llmService.analyzeAnswer(null, null, null);

        // Assert
        assertNotNull(result);
        // 应该返回模拟分析结果
        assertTrue(result.contains("{"));
    }

    @Test
    void testCreateMockQuestionJson_ContainsRequiredFields() {
        // Act
        String result = llmService.parseQuestionStructure("test");

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("questions"));
        // 模拟数据应该包含题目
        assertTrue(result.contains("questionNo") || result.contains("content"));
    }

    @Test
    void testCreateMockAnalysisResult_ContainsRequiredFields() {
        // Act
        String result = llmService.analyzeAnswer("test", "test", "test");

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("is_correct"));
        assertTrue(result.contains("error_analysis") || result.length() > 0);
    }
}
