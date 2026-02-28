package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.PaperFileRespVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.PaperCreateReqVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperFileDO;

import java.util.List;

/**
 * 试卷文件 Service 接口
 *
 * @author StudyBuddy
 */
public interface PaperFileService {

    /**
     * 创建试卷文件
     *
     * @param paperId 试卷ID
     * @param fileInfo 文件信息
     * @return 文件ID
     */
    Long createPaperFile(Long paperId, PaperCreateReqVO.FileCreateInfo fileInfo);

    /**
     * 批量创建试卷文件
     *
     * @param paperId 试卷ID
     * @param fileInfos 文件信息列表
     * @return 文件ID列表
     */
    List<Long> batchCreatePaperFiles(Long paperId, List<PaperCreateReqVO.FileCreateInfo> fileInfos);

    /**
     * 删除试卷文件
     *
     * @param id 文件ID
     */
    void deletePaperFile(Long id);

    /**
     * 根据试卷ID获取文件列表
     *
     * @param paperId 试卷ID
     * @return 文件列表
     */
    List<PaperFileDO> getPaperFilesByPaperId(Long paperId);

    /**
     * 删除试卷的所有文件
     *
     * @param paperId 试卷ID
     */
    void deletePaperFilesByPaperId(Long paperId);

}
