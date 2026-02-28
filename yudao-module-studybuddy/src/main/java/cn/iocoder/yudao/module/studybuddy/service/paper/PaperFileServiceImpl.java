package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.PaperCreateReqVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperFileDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.PaperFileMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.studybuddy.enums.ErrorCodeConstants.PAPER_FILE_NOT_EXISTS;

/**
 * 试卷文件 Service 实现类
 *
 * @author StudyBuddy
 */
@Service
@Validated
public class PaperFileServiceImpl implements PaperFileService {

    @Resource
    private PaperFileMapper paperFileMapper;

    @Override
    public Long createPaperFile(Long paperId, PaperCreateReqVO.FileCreateInfo fileInfo) {
        PaperFileDO file = PaperFileDO.builder()
                .paperId(paperId)
                .filePath(fileInfo.getFilePath())
                .fileName(fileInfo.getFileName())
                .fileType(fileInfo.getFileType())
                .fileSize(fileInfo.getFileSize())
                .sortOrder(fileInfo.getSortOrder() != null ? fileInfo.getSortOrder() : 0)
                .build();
        paperFileMapper.insert(file);
        return file.getId();
    }

    @Override
    public List<Long> batchCreatePaperFiles(Long paperId, List<PaperCreateReqVO.FileCreateInfo> fileInfos) {
        List<Long> ids = new ArrayList<>();
        if (fileInfos == null || fileInfos.isEmpty()) {
            return ids;
        }
        for (PaperCreateReqVO.FileCreateInfo fileInfo : fileInfos) {
            ids.add(createPaperFile(paperId, fileInfo));
        }
        return ids;
    }

    @Override
    public void deletePaperFile(Long id) {
        // 校验存在
        PaperFileDO file = paperFileMapper.selectById(id);
        if (file == null) {
            exception(PAPER_FILE_NOT_EXISTS);
        }
        // 删除
        paperFileMapper.deleteById(id);
    }

    @Override
    public List<PaperFileDO> getPaperFilesByPaperId(Long paperId) {
        return paperFileMapper.selectListByPaperId(paperId);
    }

    @Override
    public void deletePaperFilesByPaperId(Long paperId) {
        paperFileMapper.deleteByPaperId(paperId);
    }

}
