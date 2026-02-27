package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.AnswerSheetUploadReqVO;

import javax.validation.Valid;

/**
 * 答题卡 Service 接口
 *
 * @author StudyBuddy
 */
public interface AnswerSheetService {

    /**
     * 上传答题卡并触发处理
     *
     * @param reqVO 上传信息
     * @return 处理状态
     */
    String uploadAnswerSheet(@Valid AnswerSheetUploadReqVO reqVO);

}
