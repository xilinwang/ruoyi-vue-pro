package cn.iocoder.yudao.module.studybuddy.service.paper.event;

import cn.iocoder.yudao.module.studybuddy.enums.ocr.OcrModelEnum;
import lombok.Getter;

import java.io.Serializable;

/**
 * 试卷 OCR 处理事件
 *
 * @author StudyBuddy
 */
@Getter
public class PaperOcrEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 试卷ID
     */
    private final Long paperId;

    /**
     * 文件路径
     */
    private final String filePath;

    /**
     * OCR 模型代码
     * 默认使用阿里云读光
     */
    private final String ocrModel;

    public PaperOcrEvent(Long paperId, String filePath) {
        this.paperId = paperId;
        this.filePath = filePath;
        this.ocrModel = OcrModelEnum.ALIYUN.getCode();
    }

    public PaperOcrEvent(Long paperId, String filePath, String ocrModel) {
        this.paperId = paperId;
        this.filePath = filePath;
        this.ocrModel = ocrModel != null ? ocrModel : OcrModelEnum.ALIYUN.getCode();
    }

}
