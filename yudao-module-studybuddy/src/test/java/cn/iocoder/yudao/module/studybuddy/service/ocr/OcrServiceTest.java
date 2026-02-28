package cn.iocoder.yudao.module.studybuddy.service.ocr;

import cn.iocoder.yudao.module.studybuddy.framework.ocr.config.OcrConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OcrService 单元测试
 *
 * @author StudyBuddy
 */
@ExtendWith(MockitoExtension.class)
class OcrServiceTest {

    @InjectMocks
    private OcrServiceImpl ocrService;

    private OcrConfiguration ocrConfiguration;

    @BeforeEach
    void setUp() {
        ocrConfiguration = new OcrConfiguration();
        ocrConfiguration.setAccessKeyId("test-key-id");
        ocrConfiguration.setAccessKeySecret("test-key-secret");
        ocrConfiguration.setEndpoint("https://ocr-api.cn-shanghai.aliyuncs.com");
    }

    @Test
    void testRecognizePaper_ForEducation() {
        // Arrange
        String testFilePath = "/tmp/test-paper.jpg";

        // Act
        String result = ocrService.recognizePaperForEducation(testFilePath);

        // Assert
        assertNotNull(result);
        // 由于没有配置真实密钥，应该返回模拟数据
        assertTrue(result.contains("选择题") || result.isEmpty());
    }

    @Test
    void testRecognizePaper_WithMockData() {
        // Act
        String result = ocrService.recognizePaper("/tmp/non-existent.jpg");

        // Assert
        assertNotNull(result);
        // 应该返回模拟的试卷数据
        assertTrue(result.contains("一、选择题") || result.contains("选择题"));
    }

    @Test
    void testCreateMockResponse_ContainsExpectedContent() {
        // Act
        String result = ocrService.recognizePaper("/tmp/test.jpg");

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("选择题"));
        assertTrue(result.contains("填空题"));
        assertTrue(result.contains("简答题"));
        assertTrue(result.contains("Java"));
    }

    @Test
    void testRecognizePaper_EmptyPath() {
        // Act
        String result = ocrService.recognizePaper("");

        // Assert
        assertNotNull(result);
        // 应该返回模拟数据
        assertFalse(result.isEmpty());
    }
}
