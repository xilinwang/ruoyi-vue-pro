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

}
