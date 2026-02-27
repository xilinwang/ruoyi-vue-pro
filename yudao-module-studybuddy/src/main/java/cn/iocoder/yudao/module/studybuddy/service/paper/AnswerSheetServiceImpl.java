package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.AnswerSheetUploadReqVO;
import cn.iocoder.yudao.module.studybuddy.service.paper.event.AnswerSheetUploadEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 答题卡 Service 实现类
 *
 * @author StudyBuddy
 */
@Service
@Validated
@Slf4j
public class AnswerSheetServiceImpl implements AnswerSheetService {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public String uploadAnswerSheet(AnswerSheetUploadReqVO reqVO) {
        log.info("[uploadAnswerSheet] 上传答题卡，试卷ID: {}, 学生ID: {}, 文件路径: {}",
                 reqVO.getPaperId(), reqVO.getStudentId(), reqVO.getFilePath());

        // 发布答题卡上传事件，触发异步处理
        AnswerSheetUploadEvent event = new AnswerSheetUploadEvent(
                reqVO.getPaperId(),
                reqVO.getFilePath(),
                reqVO.getStudentId()
        );
        applicationContext.publishEvent(event);

        log.info("[uploadAnswerSheet] 答题卡上传事件已发布");
        return "PROCESSING";
    }

}
