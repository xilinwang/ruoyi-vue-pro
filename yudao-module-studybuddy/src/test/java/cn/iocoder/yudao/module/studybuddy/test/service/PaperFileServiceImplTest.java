package cn.iocoder.yudao.module.studybuddy.test.service;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.PaperCreateReqVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperDO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperFileDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.PaperFileMapper;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.PaperMapper;
import cn.iocoder.yudao.module.studybuddy.service.paper.PaperFileServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link PaperFileServiceImpl} 的单元测试类
 *
 * @author StudyBuddy
 */
@Import(PaperFileServiceImpl.class)
public class PaperFileServiceImplTest extends BaseDbUnitTest {

    @Resource
    private PaperFileServiceImpl paperFileService;

    @Resource
    private PaperFileMapper paperFileMapper;

    @Resource
    private PaperMapper paperMapper;

    @Test
    public void testCreatePaperFile_success() {
        // 先创建一个试卷
        PaperDO paper = randomPojo(PaperDO.class);
        paperMapper.insert(paper);

        // 准备参数
        PaperCreateReqVO.FileCreateInfo fileInfo = new PaperCreateReqVO.FileCreateInfo();
        fileInfo.setFilePath("/upload/test.pdf");
        fileInfo.setFileName("test.pdf");
        fileInfo.setFileType("pdf");
        fileInfo.setFileSize(1024L);
        fileInfo.setSortOrder(1);

        // 调用
        Long fileId = paperFileService.createPaperFile(paper.getId(), fileInfo);
        // 断言
        assertNotNull(fileId);
        // 校验记录的属性是否正确
        PaperFileDO fileDO = paperFileMapper.selectById(fileId);
        assertPojoEquals(fileInfo, fileDO, "sortOrder");
        assertEquals(paper.getId(), fileDO.getPaperId());
    }

    @Test
    public void testBatchCreatePaperFiles() {
        // 先创建一个试卷
        PaperDO paper = randomPojo(PaperDO.class);
        paperMapper.insert(paper);

        // 准备参数
        PaperCreateReqVO.FileCreateInfo fileInfo1 = new PaperCreateReqVO.FileCreateInfo();
        fileInfo1.setFilePath("/upload/test1.pdf");
        fileInfo1.setFileName("test1.pdf");
        fileInfo1.setFileType("pdf");
        fileInfo1.setFileSize(1024L);

        PaperCreateReqVO.FileCreateInfo fileInfo2 = new PaperCreateReqVO.FileCreateInfo();
        fileInfo2.setFilePath("/upload/test2.jpg");
        fileInfo2.setFileName("test2.jpg");
        fileInfo2.setFileType("jpg");
        fileInfo2.setFileSize(2048L);

        // 调用
        java.util.List<Long> ids = paperFileService.batchCreatePaperFiles(paper.getId(),
                java.util.Arrays.asList(fileInfo1, fileInfo2));
        // 断言
        assertEquals(2, ids.size());
        assertNotNull(ids.get(0));
        assertNotNull(ids.get(1));
    }

    @Test
    public void testGetPaperFilesByPaperId() {
        // 先创建一个试卷
        PaperDO paper = randomPojo(PaperDO.class);
        paperMapper.insert(paper);

        // mock 数据
        for (int i = 0; i < 3; i++) {
            PaperFileDO file = randomPojo(PaperFileDO.class, o -> o.setPaperId(paper.getId()));
            paperFileMapper.insert(file);
        }

        // 调用
        java.util.List<PaperFileDO> result = paperFileService.getPaperFilesByPaperId(paper.getId());
        // 断言
        assertEquals(3, result.size());
    }

    @Test
    public void testDeletePaperFile() {
        // mock 数据
        PaperFileDO dbFile = randomPojo(PaperFileDO.class);
        paperFileMapper.insert(dbFile);

        // 调用
        paperFileService.deletePaperFile(dbFile.getId());
        // 校验数据不存在了
        assertNull(paperFileMapper.selectById(dbFile.getId()));
    }

    @Test
    public void testDeletePaperFilesByPaperId() {
        // 先创建一个试卷
        PaperDO paper = randomPojo(PaperDO.class);
        paperMapper.insert(paper);

        // mock 数据
        for (int i = 0; i < 3; i++) {
            PaperFileDO file = randomPojo(PaperFileDO.class, o -> o.setPaperId(paper.getId()));
            paperFileMapper.insert(file);
        }

        // 调用
        paperFileService.deletePaperFilesByPaperId(paper.getId());

        // 校验
        java.util.List<PaperFileDO> result = paperFileMapper.selectListByPaperId(paper.getId());
        assertTrue(result.isEmpty());
    }

}