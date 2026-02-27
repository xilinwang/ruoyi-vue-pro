package cn.iocoder.yudao.module.studybuddy.service.llm;

/**
 * LLM 服务接口
 *
 * @author StudyBuddy
 */
public interface LlmService {

    /**
     * 解析题目结构
     * 从 OCR 识别的文本中提取题目结构
     *
     * @param ocrText OCR 识别的文本内容
     * @return 解析后的题目结构（JSON格式）
     */
    String parseQuestionStructure(String ocrText);

    /**
     * 分析学生答案
     * 对比标准答案和学生答案，提供详细分析
     *
     * @param questionContent   题目内容
     * @param studentAnswer     学生答案
     * @param standardAnswer    标准答案
     * @return 答案分析结果（包含是否正确、错误分析、更优解法等）
     */
    String analyzeAnswer(String questionContent, String studentAnswer, String standardAnswer);

}
