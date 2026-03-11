package cn.iocoder.yudao.module.studybuddy.service.ocr;

/**
 * OCR 服务接口
 *
 * @author StudyBuddy
 */
public interface OcrService {

    /**
     * 识别试卷图片，提取文本内容
     *
     * @param imageFilePath 图片文件路径
     * @return OCR 识别结果（JSON格式的文本内容）
     */
    String recognizePaper(String imageFilePath);

    /**
     * 识别试卷图片，提取文本内容（支持教育场景）
     *
     * @param imageFilePath 图片文件路径
     * @return OCR 识别结果
     */
    String recognizePaperForEducation(String imageFilePath);

    /**
     * 使用指定模型识别试卷图片
     *
     * @param imageFilePath 图片文件路径
     * @param ocrModel OCR 模型代码（aliyun/iflow）
     * @return OCR 识别结果
     */
    String recognizePaperWithModel(String imageFilePath, String ocrModel);

    /**
     * 使用指定模型和科目识别试卷图片
     *
     * @param imageFilePath 图片文件路径
     * @param ocrModel OCR 模型代码（aliyun/iflow）
     * @param subject 试卷科目（用于教育试卷OCR）
     * @return OCR 识别结果
     */
    String recognizePaperWithModelAndSubject(String imageFilePath, String ocrModel, String subject);

}
