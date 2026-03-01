package cn.iocoder.yudao.module.studybuddy.test.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.*;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.PaperMapper;
import cn.iocoder.yudao.module.studybuddy.enums.paper.PaperStatusEnum;
import cn.iocoder.yudao.module.studybuddy.service.paper.PaperFileServiceImpl;
import cn.iocoder.yudao.module.studybuddy.service.paper.PaperServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static cn.iocoder.yudao.module.studybuddy.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link PaperServiceImpl} 的单元测试类
 *
 * @author StudyBuddy
 */
@Import({PaperServiceImpl.class, PaperFileServiceImpl.class})
@RecordApplicationEvents
public class PaperServiceImplTest extends BaseDbUnitTest {

    @Resource
    private PaperServiceImpl paperService;

    @Resource
    private PaperMapper paperMapper;

    @Resource
    private ApplicationEvents applicationEvents;

    @Test
    public void testCreatePaper_success() {
        // 准备参数
        PaperCreateReqVO reqVO = randomPojo(PaperCreateReqVO.class, o -> {
            o.setTitle("期中考试数学试卷");
            o.setSubject("数学");
            o.setPaperNo(null); // 不指定编号，测试自动生成
        });

        // 调用
        Long paperId = paperService.createPaper(reqVO);
        // 断言
        assertNotNull(paperId);
        // 校验记录的属性是否正确
        PaperDO paperDO = paperMapper.selectById(paperId);
        assertPojoEquals(reqVO, paperDO, "id", "files", "paperNo");
        assertEquals(PaperStatusEnum.UPLOADED.getCode(), paperDO.getStatus());
        assertNotNull(paperDO.getPaperNo()); // 验证自动生成了编号
    }

    @Test
    public void testCreatePaper_autoGeneratePaperNo() {
        // 准备参数 - 不指定 paperNo
        PaperCreateReqVO reqVO = randomPojo(PaperCreateReqVO.class, o -> {
            o.setPaperNo(null);
            o.setTitle("期末考试英语试卷");
            o.setSubject("英语");
        });

        // 调用
        Long paperId = paperService.createPaper(reqVO);
        // 断言
        assertNotNull(paperId);
        PaperDO paperDO = paperMapper.selectById(paperId);
        assertNotNull(paperDO.getPaperNo());
        // 验证格式：科目代码 + 日期 + 序号
        assertTrue(paperDO.getPaperNo().matches(".*\\d{8}\\d{4}"));
    }

    @Test
    public void testCreatePaper_withFiles() {
        // 准备参数 - 带文件
        PaperCreateReqVO.FileCreateInfo fileInfo = new PaperCreateReqVO.FileCreateInfo();
        fileInfo.setFilePath("/upload/papers/test.pdf");
        fileInfo.setFileName("test.pdf");
        fileInfo.setFileType("pdf");
        fileInfo.setFileSize(1024L);

        PaperCreateReqVO reqVO = randomPojo(PaperCreateReqVO.class, o -> {
            o.setTitle("带文件的试卷");
            o.setSubject("数学");
            o.setFiles(java.util.Collections.singletonList(fileInfo));
        });

        // 调用
        Long paperId = paperService.createPaper(reqVO);
        // 断言
        assertNotNull(paperId);
        PaperDO paperDO = paperMapper.selectById(paperId);
        assertNotNull(paperDO);
    }

    @Test
    public void testCreatePaper_withPaperNo() {
        // 准备参数 - 指定 paperNo
        PaperCreateReqVO reqVO = randomPojo(PaperCreateReqVO.class, o -> {
            o.setPaperNo("TEST20250101001");
            o.setTitle("指定编号的试卷");
            o.setSubject("数学");
        });

        // 调用
        Long paperId = paperService.createPaper(reqVO);
        // 断言
        assertNotNull(paperId);
        PaperDO paperDO = paperMapper.selectById(paperId);
        assertEquals("TEST20250101001", paperDO.getPaperNo());
    }

    @Test
    public void testCreatePaper_paperNoExists() {
        // 先创建一个试卷
        PaperDO existingPaper = randomPojo(PaperDO.class, o -> o.setPaperNo("EXIST20250101001"));
        paperMapper.insert(existingPaper);

        // 准备参数 - 使用已存在的 paperNo
        PaperCreateReqVO reqVO = randomPojo(PaperCreateReqVO.class, o -> o.setPaperNo("EXIST20250101001"));

        // 调用, 并断言异常
        assertServiceException(() -> paperService.createPaper(reqVO), PAPER_NO_EXISTS);
    }

    @Test
    public void testUpdatePaper_success() {
        // mock 数据
        PaperDO dbPaper = randomPojo(PaperDO.class);
        paperMapper.insert(dbPaper);

        // 准备参数
        PaperUpdateReqVO reqVO = randomPojo(PaperUpdateReqVO.class, o -> {
            o.setId(dbPaper.getId());
        });

        // 调用
        paperService.updatePaper(reqVO);
        // 校验是否更新正确
        PaperDO paperDO = paperMapper.selectById(reqVO.getId());
        assertPojoEquals(reqVO, paperDO, "files");
    }

    @Test
    public void testDeletePaper_success() {
        // mock 数据
        PaperDO dbPaper = randomPojo(PaperDO.class);
        paperMapper.insert(dbPaper);

        // 准备参数
        Long id = dbPaper.getId();

        // 调用
        paperService.deletePaper(id);
        // 校验数据不存在了
        assertNull(paperMapper.selectById(id));
    }

    @Test
    public void testGetPaper() {
        // mock 数据
        PaperDO dbPaper = randomPojo(PaperDO.class);
        paperMapper.insert(dbPaper);

        // 调用
        PaperDO result = paperService.getPaper(dbPaper.getId());
        // 断言
        assertNotNull(result);
        assertPojoEquals(dbPaper, result);
    }

    @Test
    public void testGetPaperPage() {
        // mock 数据
        for (int i = 0; i < 3; i++) {
            PaperDO paper = randomPojo(PaperDO.class);
            paperMapper.insert(paper);
        }

        // 准备参数
        PaperPageReqVO reqVO = new PaperPageReqVO();

        // 调用
        PageResult<PaperDO> result = paperService.getPaperPage(reqVO);
        // 断言
        assertEquals(3, result.getTotal());
    }

    @Test
    public void testUpdatePaperStatus() {
        // mock 数据
        PaperDO dbPaper = randomPojo(PaperDO.class);
        paperMapper.insert(dbPaper);

        // 调用
        paperService.updatePaperStatus(dbPaper.getId(), PaperStatusEnum.READY.getCode());

        // 校验
        PaperDO paperDO = paperMapper.selectById(dbPaper.getId());
        assertEquals(PaperStatusEnum.READY.getCode(), paperDO.getStatus());
    }

    @Test
    public void testTriggerAnalyze_success() {
        // mock 数据 - 状态为 READY
        PaperDO dbPaper = randomPojo(PaperDO.class, o -> o.setStatus(PaperStatusEnum.READY.getCode()));
        paperMapper.insert(dbPaper);

        // 调用
        paperService.triggerAnalyze(dbPaper.getId());

        // 验证事件发布
        assertTrue(applicationEvents.stream(cn.iocoder.yudao.module.studybuddy.service.paper.event.PaperAnalyzeEvent.class).count() > 0);
    }

    @Test
    public void testTriggerAnalyze_statusNotReady() {
        // mock 数据 - 状态不是 READY
        PaperDO dbPaper = randomPojo(PaperDO.class, o -> o.setStatus(PaperStatusEnum.UPLOADED.getCode()));
        paperMapper.insert(dbPaper);

        // 调用, 并断言异常
        assertServiceException(() -> paperService.triggerAnalyze(dbPaper.getId()), PAPER_STATUS_NOT_READY);
    }

    @Test
    public void testTriggerOcr_success() {
        // mock 数据 - 状态为 UPLOADED，有文件
        PaperDO dbPaper = randomPojo(PaperDO.class, o -> {
            o.setStatus(PaperStatusEnum.UPLOADED.getCode());
            o.setFilePath("/upload/test.pdf");
        });
        paperMapper.insert(dbPaper);

        // 调用
        paperService.triggerOcr(dbPaper.getId());

        // 验证状态更新
        PaperDO paperDO = paperMapper.selectById(dbPaper.getId());
        assertEquals(PaperStatusEnum.OCR_PROCESSING.getCode(), paperDO.getStatus());

        // 验证事件发布
        assertTrue(applicationEvents.stream(cn.iocoder.yudao.module.studybuddy.service.paper.event.PaperOcrEvent.class).count() > 0);
    }

    @Test
    public void testTriggerOcr_statusNotUploaded() {
        // mock 数据 - 状态不是 UPLOADED
        PaperDO dbPaper = randomPojo(PaperDO.class, o -> o.setStatus(PaperStatusEnum.READY.getCode()));
        paperMapper.insert(dbPaper);

        // 调用, 并断言异常
        assertServiceException(() -> paperService.triggerOcr(dbPaper.getId()), PAPER_STATUS_NOT_UPLOADED);
    }

    @Test
    public void testGetPaperWithQuestions() {
        // mock 数据
        PaperDO dbPaper = randomPojo(PaperDO.class);
        paperMapper.insert(dbPaper);

        // 调用
        PaperWithQuestionsRespVO result = paperService.getPaperWithQuestions(dbPaper.getId());
        // 断言
        assertNotNull(result);
        assertEquals(dbPaper.getId(), result.getId());
        assertNotNull(result.getQuestions());
    }

}