package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.*;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperDO;

import javax.validation.Valid;

/**
 * 试卷 Service 接口
 *
 * @author StudyBuddy
 */
public interface PaperService {

    /**
     * 创建试卷
     *
     * @param createReqVO 创建信息
     * @return 试卷ID
     */
    Long createPaper(@Valid PaperCreateReqVO createReqVO);

    /**
     * 更新试卷
     *
     * @param updateReqVO 更新信息
     */
    void updatePaper(@Valid PaperUpdateReqVO updateReqVO);

    /**
     * 删除试卷
     *
     * @param id 试卷ID
     */
    void deletePaper(Long id);

    /**
     * 获取试卷详情
     *
     * @param id 试卷ID
     * @return 试卷详情
     */
    PaperDO getPaper(Long id);

    /**
     * 获取试卷分页
     *
     * @param pageReqVO 分页查询条件
     * @return 试卷分页结果
     */
    PageResult<PaperDO> getPaperPage(PaperPageReqVO pageReqVO);

    /**
     * 更新试卷状态
     *
     * @param id     试卷ID
     * @param status 新状态
     */
    void updatePaperStatus(Long id, String status);

    /**
     * 更新试卷状态（带错误信息）
     *
     * @param id        试卷ID
     * @param status    新状态
     * @param errorMsg  错误信息
     */
    void updatePaperStatus(Long id, String status, String errorMsg);

    /**
     * 触发试卷分析
     *
     * @param id 试卷ID
     */
    void triggerAnalyze(Long id);

}
