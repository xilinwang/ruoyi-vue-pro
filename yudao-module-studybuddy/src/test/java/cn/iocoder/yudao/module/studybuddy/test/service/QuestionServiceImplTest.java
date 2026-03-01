package cn.iocoder.yudao.module.studybuddy.test.service;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.QuestionStandardAnswerUpdateReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.QuestionVerifyReqVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperDO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.QuestionDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.PaperMapper;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.paper.QuestionMapper;
import cn.iocoder.yudao.module.studybuddy.service.paper.QuestionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link QuestionServiceImpl} 的单元测试类
 *
 * @author StudyBuddy
 */
@Import(QuestionServiceImpl.class)
public class QuestionServiceImplTest extends BaseDbUnitTest {

    @Resource
    private QuestionServiceImpl questionService;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private PaperMapper paperMapper;

    @Test
    public void testGetQuestion() {
        // mock 数据
        QuestionDO dbQuestion = randomPojo(QuestionDO.class);
        questionMapper.insert(dbQuestion);

        // 调用
        QuestionDO result = questionService.getQuestion(dbQuestion.getId());
        // 断言
        assertNotNull(result);
        assertPojoEquals(dbQuestion, result);
    }

    @Test
    public void testGetQuestionListByPaperId() {
        // 先创建一个试卷
        PaperDO paper = randomPojo(PaperDO.class);
        paperMapper.insert(paper);

        // mock 数据
        for (int i = 0; i < 3; i++) {
            QuestionDO question = randomPojo(QuestionDO.class, o -> o.setPaperId(paper.getId()));
            questionMapper.insert(question);
        }

        // 准备另一个试卷的题目
        PaperDO otherPaper = randomPojo(PaperDO.class);
        paperMapper.insert(otherPaper);
        QuestionDO otherQuestion = randomPojo(QuestionDO.class, o -> o.setPaperId(otherPaper.getId()));
        questionMapper.insert(otherQuestion);

        // 调用
        java.util.List<QuestionDO> result = questionService.getQuestionListByPaperId(paper.getId());
        // 断言
        assertEquals(3, result.size());
    }

    @Test
    public void testUpdateStandardAnswer() {
        // mock 数据
        QuestionDO dbQuestion = randomPojo(QuestionDO.class);
        questionMapper.insert(dbQuestion);

        // 准备参数
        QuestionStandardAnswerUpdateReqVO reqVO = new QuestionStandardAnswerUpdateReqVO();
        reqVO.setId(dbQuestion.getId());
        reqVO.setStandardAnswer("这是标准答案");

        // 调用
        questionService.updateStandardAnswer(reqVO);

        // 校验
        QuestionDO questionDO = questionMapper.selectById(dbQuestion.getId());
        assertEquals("这是标准答案", questionDO.getStandardAnswer());
        assertFalse(questionDO.getStandardAnswerVerified());
    }

    @Test
    public void testVerifyStandardAnswer_verifyTrue() {
        // mock 数据 - 有标准答案
        QuestionDO dbQuestion = randomPojo(QuestionDO.class, o -> {
            o.setStandardAnswer("标准答案");
            o.setStandardAnswerVerified(false);
        });
        questionMapper.insert(dbQuestion);

        // 准备参数
        QuestionVerifyReqVO reqVO = new QuestionVerifyReqVO();
        reqVO.setId(dbQuestion.getId());
        reqVO.setVerified(true);

        // 调用
        questionService.verifyStandardAnswer(reqVO);

        // 校验
        QuestionDO questionDO = questionMapper.selectById(dbQuestion.getId());
        assertTrue(questionDO.getStandardAnswerVerified());
    }

    @Test
    public void testVerifyStandardAnswer_verifyFalse() {
        // mock 数据 - 已确认
        QuestionDO dbQuestion = randomPojo(QuestionDO.class, o -> {
            o.setStandardAnswer("标准答案");
            o.setStandardAnswerVerified(true);
        });
        questionMapper.insert(dbQuestion);

        // 准备参数
        QuestionVerifyReqVO reqVO = new QuestionVerifyReqVO();
        reqVO.setId(dbQuestion.getId());
        reqVO.setVerified(false);

        // 调用
        questionService.verifyStandardAnswer(reqVO);

        // 校验
        QuestionDO questionDO = questionMapper.selectById(dbQuestion.getId());
        assertFalse(questionDO.getStandardAnswerVerified());
    }

    @Test
    public void testVerifyStandardAnswer_noAnswer() {
        // mock 数据 - 没有标准答案
        QuestionDO dbQuestion = randomPojo(QuestionDO.class, o -> o.setStandardAnswer(null));
        questionMapper.insert(dbQuestion);

        // 准备参数
        QuestionVerifyReqVO reqVO = new QuestionVerifyReqVO();
        reqVO.setId(dbQuestion.getId());
        reqVO.setVerified(true);

        // 调用, 并断言异常
        assertThrows(IllegalArgumentException.class, () -> questionService.verifyStandardAnswer(reqVO));
    }

    @Test
    public void testBatchVerifyStandardAnswers() {
        // 先创建一个试卷
        PaperDO paper = randomPojo(PaperDO.class);
        paperMapper.insert(paper);

        // mock 数据 - 有标准答案但未确认的题目
        for (int i = 0; i < 3; i++) {
            final int index = i;
            QuestionDO question = randomPojo(QuestionDO.class, o -> {
                o.setPaperId(paper.getId());
                o.setStandardAnswer("标准答案" + index);
                o.setStandardAnswerVerified(false);
            });
            questionMapper.insert(question);
        }

        // mock 数据 - 没有标准答案的题目
        QuestionDO noAnswerQuestion = randomPojo(QuestionDO.class, o -> {
            o.setPaperId(paper.getId());
            o.setStandardAnswer(null);
            o.setStandardAnswerVerified(false);
        });
        questionMapper.insert(noAnswerQuestion);

        // 调用
        questionService.batchVerifyStandardAnswers(paper.getId());

        // 校验 - 只有有标准答案的题目被确认
        java.util.List<QuestionDO> questions = questionMapper.selectListByPaperId(paper.getId());
        for (QuestionDO question : questions) {
            if (question.getStandardAnswer() != null) {
                assertTrue(question.getStandardAnswerVerified());
            }
        }
    }

    @Test
    public void testGetUnverifiedCountByPaperId() {
        // 先创建一个试卷
        PaperDO paper = randomPojo(PaperDO.class);
        paperMapper.insert(paper);

        // mock 数据
        for (int i = 0; i < 2; i++) {
            final int index = i;
            QuestionDO question = randomPojo(QuestionDO.class, o -> {
                o.setPaperId(paper.getId());
                o.setStandardAnswer("标准答案" + index);
                o.setStandardAnswerVerified(false);
            });
            questionMapper.insert(question);
        }

        // mock 数据 - 已确认的题目
        QuestionDO verifiedQuestion = randomPojo(QuestionDO.class, o -> {
            o.setPaperId(paper.getId());
            o.setStandardAnswer("已确认的答案");
            o.setStandardAnswerVerified(true);
        });
        questionMapper.insert(verifiedQuestion);

        // 调用
        Long count = questionService.getUnverifiedCountByPaperId(paper.getId());
        // 断言
        assertEquals(2L, count);
    }

}