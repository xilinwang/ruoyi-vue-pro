package cn.iocoder.yudao.module.studybuddy.test.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookExportReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookPageReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookRespVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.WrongBookDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.stats.WrongBookMapper;
import cn.iocoder.yudao.module.studybuddy.service.stats.WrongBookServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static cn.iocoder.yudao.module.studybuddy.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link WrongBookServiceImpl} 的单元测试类
 *
 * @author StudyBuddy
 */
@Import(WrongBookServiceImpl.class)
public class WrongBookServiceImplTest extends BaseDbUnitTest {

    @Resource
    private WrongBookServiceImpl wrongBookService;

    @Resource
    private WrongBookMapper wrongBookMapper;

    @Test
    public void testCreateWrongBook_success() {
        // 准备参数
        Long userId = randomLongId();
        WrongBookDO wrongBook = randomPojo(WrongBookDO.class, o -> {
            o.setUserId(userId);
            o.setQuestionId(randomLongId());
        });

        // 调用
        wrongBookService.addWrongBook(wrongBook);
        // 断言 - 应该新增了一条记录
        java.util.List<WrongBookDO> result = wrongBookMapper.selectByUserId(userId);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getWrongCount());
    }

    @Test
    public void testCreateWrongBook_existingQuestion() {
        // 准备参数
        Long userId = randomLongId();
        Long questionId = randomLongId();

        // 先创建一条错题记录
        WrongBookDO existingWrongBook = randomPojo(WrongBookDO.class, o -> {
            o.setUserId(userId);
            o.setQuestionId(questionId);
            o.setWrongCount(1);
            o.setIsMastered(true);
        });
        wrongBookMapper.insert(existingWrongBook);

        // 准备参数 - 同一用户同一题目
        WrongBookDO newWrongBook = randomPojo(WrongBookDO.class, o -> {
            o.setUserId(userId);
            o.setQuestionId(questionId);
        });

        // 调用
        wrongBookService.addWrongBook(newWrongBook);

        // 断言 - 应该更新了错误次数
        WrongBookDO updatedWrongBook = wrongBookMapper.selectById(existingWrongBook.getId());
        assertEquals(2, updatedWrongBook.getWrongCount());
        assertFalse(updatedWrongBook.getIsMastered());
    }

    @Test
    public void testGetWrongBookPage() {
        // 准备参数
        Long userId = randomLongId();
        for (int i = 0; i < 3; i++) {
            WrongBookDO wrongBook = randomPojo(WrongBookDO.class, o -> o.setUserId(userId));
            wrongBookMapper.insert(wrongBook);
        }

        // 准备另一个用户的错题
        Long otherUserId = randomLongId();
        WrongBookDO otherWrongBook = randomPojo(WrongBookDO.class, o -> o.setUserId(otherUserId));
        wrongBookMapper.insert(otherWrongBook);

        // 调用
        WrongBookPageReqVO reqVO = new WrongBookPageReqVO();
        PageResult<WrongBookRespVO> result = wrongBookService.getWrongBookPage(userId, reqVO);
        // 断言
        assertEquals(3, result.getTotal());
    }

    @Test
    public void testGetWrongBook() {
        // mock 数据
        Long userId = randomLongId();
        WrongBookDO dbWrongBook = randomPojo(WrongBookDO.class, o -> o.setUserId(userId));
        wrongBookMapper.insert(dbWrongBook);

        // 调用
        WrongBookRespVO result = wrongBookService.getWrongBook(userId, dbWrongBook.getId());
        // 断言
        assertNotNull(result);
        assertEquals(dbWrongBook.getId(), result.getId());
    }

    @Test
    public void testGetWrongBook_notExists() {
        // 准备参数
        Long userId = randomLongId();
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> wrongBookService.getWrongBook(userId, id), WRONG_BOOK_NOT_EXISTS);
    }

    @Test
    public void testUpdateMasteredStatus() {
        // mock 数据
        Long userId = randomLongId();
        WrongBookDO dbWrongBook = randomPojo(WrongBookDO.class, o -> {
            o.setUserId(userId);
            o.setIsMastered(false);
        });
        wrongBookMapper.insert(dbWrongBook);

        // 调用
        wrongBookService.updateMasteredStatus(userId, dbWrongBook.getId(), true);

        // 校验
        WrongBookDO updatedWrongBook = wrongBookMapper.selectById(dbWrongBook.getId());
        assertTrue(updatedWrongBook.getIsMastered());
        assertNotNull(updatedWrongBook.getMasteredTime());
    }

    @Test
    public void testDeleteWrongBook() {
        // mock 数据
        Long userId = randomLongId();
        WrongBookDO dbWrongBook = randomPojo(WrongBookDO.class, o -> o.setUserId(userId));
        wrongBookMapper.insert(dbWrongBook);

        // 调用
        wrongBookService.deleteWrongBook(userId, dbWrongBook.getId());
        // 校验数据不存在了
        assertNull(wrongBookMapper.selectById(dbWrongBook.getId()));
    }

    @Test
    public void testDeleteWrongBook_notExists() {
        // 准备参数
        Long userId = randomLongId();
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> wrongBookService.deleteWrongBook(userId, id), WRONG_BOOK_NOT_EXISTS);
    }

    @Test
    public void testGetWrongBookList() {
        // mock 数据
        Long userId = randomLongId();
        for (int i = 0; i < 3; i++) {
            WrongBookDO wrongBook = randomPojo(WrongBookDO.class, o -> o.setUserId(userId));
            wrongBookMapper.insert(wrongBook);
        }

        // 准备另一个用户的错题
        Long otherUserId = randomLongId();
        WrongBookDO otherWrongBook = randomPojo(WrongBookDO.class, o -> o.setUserId(otherUserId));
        wrongBookMapper.insert(otherWrongBook);

        // 调用
        java.util.List<WrongBookDO> result = wrongBookService.getWrongBookList(userId);
        // 断言
        assertEquals(3, result.size());
    }

    @Test
    public void testExportWrongBook_invalidFormat_excel() {
        // 准备参数
        Long userId = randomLongId();
        // 创建一些错题数据
        WrongBookDO wrongBook = randomPojo(WrongBookDO.class, o -> o.setUserId(userId));
        wrongBookMapper.insert(wrongBook);

        // 准备导出请求 - 使用不支持的格式 excel
        WrongBookExportReqVO reqVO = new WrongBookExportReqVO();
        reqVO.setFormat("excel");

        // 调用, 并断言异常
        assertServiceException(() -> wrongBookService.exportWrongBook(userId, reqVO, null),
                WRONG_BOOK_EXPORT_FORMAT_INVALID);
    }

    @Test
    public void testExportWrongBook_invalidFormat_csv() {
        // 准备参数
        Long userId = randomLongId();

        // 准备导出请求 - 使用不支持的格式 csv
        WrongBookExportReqVO reqVO = new WrongBookExportReqVO();
        reqVO.setFormat("csv");

        // 调用, 并断言异常
        assertServiceException(() -> wrongBookService.exportWrongBook(userId, reqVO, null),
                WRONG_BOOK_EXPORT_FORMAT_INVALID);
    }

    @Test
    public void testExportWrongBook_validFormat_docx() {
        // 准备参数
        Long userId = randomLongId();

        // 准备导出请求 - 使用有效的格式 docx
        WrongBookExportReqVO reqVO = new WrongBookExportReqVO();
        reqVO.setFormat("docx");

        // 调用 - 由于 response 为 null，会在后面抛异常，但格式验证应该通过
        // 这里主要测试格式验证逻辑不会在 docx 格式时抛 WRONG_BOOK_EXPORT_FORMAT_INVALID
        try {
            wrongBookService.exportWrongBook(userId, reqVO, null);
        } catch (NullPointerException | IllegalStateException expected) {
            // 预期在设置 response header 时抛异常（因为 response 是 null）
            // 但不应该抛出 WRONG_BOOK_EXPORT_FORMAT_INVALID
        }
    }

    @Test
    public void testExportWrongBook_validFormat_pdf() {
        // 准备参数
        Long userId = randomLongId();

        // 准备导出请求 - 使用有效的格式 pdf
        WrongBookExportReqVO reqVO = new WrongBookExportReqVO();
        reqVO.setFormat("PDF"); // 测试大小写不敏感

        // 调用 - 由于 response 为 null，会在后面抛异常，但格式验证应该通过
        try {
            wrongBookService.exportWrongBook(userId, reqVO, null);
        } catch (NullPointerException | IllegalStateException expected) {
            // 预期在设置 response header 时抛异常（因为 response 是 null）
            // 但不应该抛出 WRONG_BOOK_EXPORT_FORMAT_INVALID
        }
    }

    @Test
    public void testExportWrongBook_defaultFormat() {
        // 准备参数
        Long userId = randomLongId();

        // 准备导出请求 - 不指定格式（默认应该是 docx）
        WrongBookExportReqVO reqVO = new WrongBookExportReqVO();
        // 不设置 format，默认为 docx

        // 调用 - 格式验证应该通过，不会抛 WRONG_BOOK_EXPORT_FORMAT_INVALID
        try {
            wrongBookService.exportWrongBook(userId, reqVO, null);
        } catch (NullPointerException | IllegalStateException expected) {
            // 预期在设置 response header 时抛异常（因为 response 是 null）
            // 但不应该抛出 WRONG_BOOK_EXPORT_FORMAT_INVALID
        }
    }

}