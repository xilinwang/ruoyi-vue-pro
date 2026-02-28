package cn.iocoder.yudao.module.studybuddy.service.ocr;

import cn.iocoder.yudao.module.studybuddy.enums.ocr.OcrModelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * OCR 服务工厂
 *
 * 根据模型类型选择对应的 OCR 服务实现
 *
 * @author StudyBuddy
 */
@Service
@Slf4j
public class OcrServiceFactory {

    @Resource
    private OcrServiceImpl aliyunOcrService;

    @Resource
    private IflowOcrServiceImpl iflowOcrService;

    /**
     * 获取指定模型的 OCR 服务
     *
     * @param ocrModel OCR 模型代码
     * @return OCR 服务实例
     */
    public OcrService getOcrService(String ocrModel) {
        OcrModelEnum model = OcrModelEnum.getByCode(ocrModel);

        switch (model) {
            case IFLOW:
                log.debug("[getOcrService] 使用 iFlow OCR 服务");
                return iflowOcrService;
            case ALIYUN:
            default:
                log.debug("[getOcrService] 使用阿里云 OCR 服务");
                return aliyunOcrService;
        }
    }

    /**
     * 获取默认 OCR 服务（阿里云）
     */
    public OcrService getDefaultOcrService() {
        return aliyunOcrService;
    }

}
