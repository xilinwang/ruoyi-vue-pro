package cn.iocoder.yudao.module.studybuddy.service.stats;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookExportReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookPageReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookRespVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.WrongBookDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.stats.WrongBookMapper;
import cn.iocoder.yudao.module.studybuddy.enums.ErrorCodeConstants;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 错题本 Service 实现类
 *
 * @author StudyBuddy
 */
@Service
@Validated
@Slf4j
public class WrongBookServiceImpl implements WrongBookService {

    @Resource
    private WrongBookMapper wrongBookMapper;

    @Override
    public PageResult<WrongBookRespVO> getWrongBookPage(Long userId, WrongBookPageReqVO reqVO) {
        PageResult<WrongBookDO> pageResult = wrongBookMapper.selectPage(userId, reqVO);
        List<WrongBookRespVO> list = pageResult.getList().stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public WrongBookRespVO getWrongBook(Long userId, Long id) {
        WrongBookDO wrongBook = wrongBookMapper.selectById(id);
        if (wrongBook == null || !wrongBook.getUserId().equals(userId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.WRONG_BOOK_NOT_EXISTS);
        }
        return convertToRespVO(wrongBook);
    }

    @Override
    public void addWrongBook(WrongBookDO wrongBook) {
        // 检查是否已存在
        WrongBookDO existing = wrongBookMapper.selectByUserAndQuestion(
                wrongBook.getUserId(), wrongBook.getQuestionId());
        if (existing != null) {
            // 更新错误次数
            existing.setWrongCount(existing.getWrongCount() + 1);
            existing.setLastWrongTime(LocalDateTime.now());
            existing.setIsMastered(false);
            wrongBookMapper.updateById(existing);
            log.info("[addWrongBook] 更新错题错误次数: id={}, wrongCount={}", existing.getId(), existing.getWrongCount());
        } else {
            // 新增错题
            wrongBook.setWrongCount(1);
            wrongBook.setLastWrongTime(LocalDateTime.now());
            wrongBook.setIsMastered(false);
            wrongBookMapper.insert(wrongBook);
            log.info("[addWrongBook] 新增错题: userId={}, questionId={}", wrongBook.getUserId(), wrongBook.getQuestionId());
        }
    }

    @Override
    public void updateMasteredStatus(Long userId, Long id, Boolean isMastered) {
        WrongBookDO wrongBook = wrongBookMapper.selectById(id);
        if (wrongBook == null || !wrongBook.getUserId().equals(userId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.WRONG_BOOK_NOT_EXISTS);
        }
        wrongBook.setIsMastered(isMastered);
        if (isMastered) {
            wrongBook.setMasteredTime(LocalDateTime.now());
        }
        wrongBookMapper.updateById(wrongBook);
        log.info("[updateMasteredStatus] 更新掌握状态: id={}, isMastered={}", id, isMastered);
    }

    @Override
    public void deleteWrongBook(Long userId, Long id) {
        WrongBookDO wrongBook = wrongBookMapper.selectById(id);
        if (wrongBook == null || !wrongBook.getUserId().equals(userId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.WRONG_BOOK_NOT_EXISTS);
        }
        wrongBookMapper.deleteById(id);
        log.info("[deleteWrongBook] 删除错题: id={}", id);
    }

    @Override
    public void exportWrongBook(Long userId, WrongBookExportReqVO reqVO, HttpServletResponse response) {
        // 校验导出格式，只支持 Word 和 PDF
        String format = reqVO.getFormat();
        if (format == null || format.isEmpty()) {
            format = "docx"; // 默认导出 Word 格式
        }

        // 只支持 docx 和 pdf 两种格式
        if (!"pdf".equalsIgnoreCase(format) && !"docx".equalsIgnoreCase(format)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.WRONG_BOOK_EXPORT_FORMAT_INVALID);
        }

        // 获取要导出的错题列表
        List<WrongBookDO> wrongBooks;
        if (reqVO.getIds() != null && !reqVO.getIds().isEmpty()) {
            wrongBooks = wrongBookMapper.selectByIds(userId, reqVO.getIds());
        } else {
            wrongBooks = wrongBookMapper.selectByUserId(userId);
        }

        // 过滤未掌握的
        if (Boolean.TRUE.equals(reqVO.getOnlyNotMastered())) {
            wrongBooks = wrongBooks.stream()
                    .filter(wb -> !Boolean.TRUE.equals(wb.getIsMastered()))
                    .collect(Collectors.toList());
        }

        // 导出
        try {
            if ("pdf".equalsIgnoreCase(format)) {
                exportAsPdf(wrongBooks, response);
            } else {
                exportAsDocx(wrongBooks, response);
            }
        } catch (IOException e) {
            log.error("[exportWrongBook] 导出失败", e);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.WRONG_BOOK_EXPORT_FAILED);
        }
    }

    @Override
    public List<WrongBookDO> getWrongBookList(Long userId) {
        return wrongBookMapper.selectByUserId(userId);
    }

    private void exportAsDocx(List<WrongBookDO> wrongBooks, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        String fileName = URLEncoder.encode("错题本_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".docx");

        try (XWPFDocument document = new XWPFDocument()) {
            // 标题
            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText("错题本");
            titleRun.setBold(true);
            titleRun.setFontSize(20);

            // 导出时间
            XWPFParagraph datePara = document.createParagraph();
            XWPFRun dateRun = datePara.createRun();
            dateRun.setText("导出时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            dateRun.setFontSize(10);

            // 错题内容
            int index = 1;
            for (WrongBookDO wb : wrongBooks) {
                // 题号
                XWPFParagraph questionPara = document.createParagraph();
                XWPFRun questionRun = questionPara.createRun();
                questionRun.setText(index + ". " + (wb.getQuestionNo() != null ? wb.getQuestionNo() : "题目"));
                questionRun.setBold(true);

                // 题目内容
                if (wb.getQuestionContent() != null) {
                    XWPFParagraph contentPara = document.createParagraph();
                    XWPFRun contentRun = contentPara.createRun();
                    contentRun.setText("题目：" + wb.getQuestionContent());
                }

                // 知识点
                if (wb.getKnowledgePoint() != null) {
                    XWPFParagraph kpPara = document.createParagraph();
                    XWPFRun kpRun = kpPara.createRun();
                    kpRun.setText("知识点：" + wb.getKnowledgePoint());
                }

                // 学生答案
                if (wb.getStudentAnswer() != null) {
                    XWPFParagraph studentPara = document.createParagraph();
                    XWPFRun studentRun = studentPara.createRun();
                    studentRun.setText("你的答案：" + wb.getStudentAnswer());
                    studentRun.setColor("FF0000");
                }

                // 标准答案
                if (wb.getStandardAnswer() != null) {
                    XWPFParagraph standardPara = document.createParagraph();
                    XWPFRun standardRun = standardPara.createRun();
                    standardRun.setText("标准答案：" + wb.getStandardAnswer());
                    standardRun.setColor("008000");
                }

                // 错误分析
                if (wb.getErrorAnalysis() != null) {
                    XWPFParagraph analysisPara = document.createParagraph();
                    XWPFRun analysisRun = analysisPara.createRun();
                    analysisRun.setText("错误分析：" + wb.getErrorAnalysis());
                }

                // 更优解法
                if (wb.getBetterSolution() != null) {
                    XWPFParagraph solutionPara = document.createParagraph();
                    XWPFRun solutionRun = solutionPara.createRun();
                    solutionRun.setText("更优解法：" + wb.getBetterSolution());
                }

                // 空行
                document.createParagraph();
                index++;
            }

            document.write(response.getOutputStream());
        }
    }

    private void exportAsPdf(List<WrongBookDO> wrongBooks, HttpServletResponse response) throws IOException {
        // 简单实现：使用 docx 格式（实际项目中可以使用 iText 等 PDF 库）
        // 这里先使用 docx 格式
        exportAsDocx(wrongBooks, response);
    }

    private WrongBookRespVO convertToRespVO(WrongBookDO wrongBook) {
        WrongBookRespVO respVO = new WrongBookRespVO();
        respVO.setId(wrongBook.getId());
        respVO.setQuestionId(wrongBook.getQuestionId());
        respVO.setPaperId(wrongBook.getPaperId());
        respVO.setQuestionNo(wrongBook.getQuestionNo());
        respVO.setQuestionContent(wrongBook.getQuestionContent());
        respVO.setKnowledgePoint(wrongBook.getKnowledgePoint());
        respVO.setStudentAnswer(wrongBook.getStudentAnswer());
        respVO.setStandardAnswer(wrongBook.getStandardAnswer());
        respVO.setErrorAnalysis(wrongBook.getErrorAnalysis());
        respVO.setBetterSolution(wrongBook.getBetterSolution());
        respVO.setWrongCount(wrongBook.getWrongCount());
        respVO.setLastWrongTime(wrongBook.getLastWrongTime());
        respVO.setIsMastered(wrongBook.getIsMastered());
        respVO.setRemark(wrongBook.getRemark());
        respVO.setCreateTime(wrongBook.getCreateTime());
        return respVO;
    }

}
