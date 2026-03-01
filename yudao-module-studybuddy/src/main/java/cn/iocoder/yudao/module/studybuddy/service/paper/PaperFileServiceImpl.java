package cn.iocoder.yudao.module.studybuddy.service.paper;

import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.PaperCreateReqVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperFileDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.PaperFileMapper;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PaperFileServiceImpl implements PaperFileService {

    @Resource
    private PaperFileMapper paperFileMapper;

    @Override
    public Long createPaperFile(Long paperId, PaperCreateReqVO.FileCreateInfo fileInfo) {
        log.debug("[createPaperFile] 创建试卷文件，试卷ID: {}, 文件名: {}", paperId, fileInfo.getFileName());

        try {
            PaperFileDO file = PaperFileDO.builder()
                    .paperId(paperId)
                    .filePath(fileInfo.getFilePath())
                    .fileName(fileInfo.getFileName())
                    .fileType(fileInfo.getFileType())
                    .fileSize(fileInfo.getFileSize())
                    .sortOrder(fileInfo.getSortOrder() != null ? fileInfo.getSortOrder() : 0)
                    .build();
            paperFileMapper.insert(file);
            log.debug("[createPaperFile] 试卷文件创建成功，文件ID: {}", file.getId());
            return file.getId();
        } catch (Exception e) {
            log.error("[createPaperFile] 创建试卷文件失败，试卷ID: {}", paperId, e);
            throw e;
        }
    }

    @Override
    public List<Long> batchCreatePaperFiles(Long paperId, List<PaperCreateReqVO.FileCreateInfo> fileInfos) {
        log.info("[batchCreatePaperFiles] 批量创建试卷文件，试卷ID: {}, 文件数量: {}", paperId, fileInfos.size());

        List<Long> ids = new ArrayList<>();
        if (fileInfos == null || fileInfos.isEmpty()) {
            return ids;
        }
        for (PaperCreateReqVO.FileCreateInfo fileInfo : fileInfos) {
            try {
                ids.add(createPaperFile(paperId, fileInfo));
            } catch (Exception e) {
                log.error("[batchBatchCreatePaperFiles] 批量创建试卷文件失败，试卷ID: {}, 文件名: {}",
                        paperId, fileInfo.getFileName(), e);
            }
        }

        log.info("[batchCreatePaperFiles] 批量创建试卷文件完成，成功: {}/{}", ids.size(), fileInfos.size());
        return ids;
    }

    @Override
    public void deletePaperFile(Long id) {
        log.debug("[deletePaperFile] 删除试卷文件，文件ID: {}", id);

        try {
            // 校验存在
            PaperFileDO file = paperFileMapper.selectById(id);
            if (file == null) {
                log.error("[deletePaperFile] 试卷文件不存在，文件ID: {}", id);
                throw exception(PAPER_FILE_NOT_EXISTS);
            }
            // 删除
            paperFileMapper.deleteById(id);
            log.debug("[deletePaperFile] 试卷文件删除成功，文件ID: {}", id);
        } catch (Exception e) {
            log.error("[deletePaperFile] 删除试卷文件失败，文件ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<PaperFileDO> getPaperFilesByPaperId(Long paperId) {
        return paperFileMapper.selectListByPaperId(paperId);
    }

    @Override
    public void deletePaperFilesByPaperId(Long paperId) {
        log.debug("[deletePaperFilesByPaperId] 删除试卷的所有文件，试卷ID: {}", paperId);

        try {
            paperFileMapper.deleteByPaperId(paperId);
            log.debug("[deletePaperFilesByPaperId] 试卷文件删除完成，试卷ID: {}", paperId);
        } catch (Exception e) {
            log.error("[deletePaperFilesByPaperId] 删除试卷文件失败，试卷ID: {}", paperId, e);
            throw e;
        }
    }

}
