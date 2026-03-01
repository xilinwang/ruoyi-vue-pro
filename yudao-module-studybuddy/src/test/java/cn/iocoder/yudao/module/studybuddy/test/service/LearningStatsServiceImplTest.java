package cn.iocoder.yudao.module.studybuddy.test.service;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.LearningStatsRespVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.LearningSummaryRespVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.LearningStatsDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.stats.LearningStatsMapper;
import cn.iocoder.yudao.module.studybuddy.service.stats.LearningStatsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link LearningStatsServiceImpl} 的单元测试类
 *
 * @author StudyBuddy
 */
@Import(LearningStatsServiceImpl.class)
public class LearningStatsServiceImplTest extends BaseDbUnitTest {

    @Resource
    private LearningStatsServiceImpl learningStatsService;

    @Resource
    private LearningStatsMapper learningStatsMapper;

    @Test
    public void testCreateOrUpdateStats_newStats() {
        // 准备参数
        Long userId = randomLongId();
        String knowledgePoint = "数学";

        // 调用
        learningStatsService.updateLearningStats(userId, knowledgePoint, true);

        // 断言
        LearningStatsDO stats = learningStatsMapper.selectByUserAndKnowledgePoint(userId, knowledgePoint);
        assertNotNull(stats);
        assertEquals(1, stats.getTotalCount());
        assertEquals(1, stats.getCorrectCount());
        assertEquals(0, stats.getWrongCount());
        assertEquals(new BigDecimal("100.00"), stats.getAccuracyRate());
    }

    @Test
    public void testCreateOrUpdateStats_updateStats() {
        // 准备参数
        Long userId = randomLongId();
        String knowledgePoint = "数学";

        // 先创建一条记录
        LearningStatsDO existingStats = randomPojo(LearningStatsDO.class, o -> {
            o.setUserId(userId);
            o.setKnowledgePoint(knowledgePoint);
            o.setTotalCount(1);
            o.setCorrectCount(1);
            o.setWrongCount(0);
            o.setAccuracyRate(new BigDecimal("100.00"));
            o.setMasteryLevel("EXCELLENT");
        });
        learningStatsMapper.insert(existingStats);

        // 调用 - 添加一个错误
        learningStatsService.updateLearningStats(userId, knowledgePoint, false);

        // 断言
        LearningStatsDO updatedStats = learningStatsMapper.selectByUserAndKnowledgePoint(userId, knowledgePoint);
        assertEquals(2, updatedStats.getTotalCount());
        assertEquals(1, updatedStats.getCorrectCount());
        assertEquals(1, updatedStats.getWrongCount());
    }

    @Test
    public void testGetStatsByUserId() {
        // mock 数据
        Long userId = randomLongId();
        for (int i = 0; i < 3; i++) {
            final int index = i;
            LearningStatsDO stats = randomPojo(LearningStatsDO.class, o -> {
                o.setUserId(userId);
                o.setAccuracyRate(new BigDecimal(String.valueOf(70 + index * 10)));
                o.setMasteryLevel(index == 0 ? "EXCELLENT" : "GOOD");
            });
            learningStatsMapper.insert(stats);
        }

        // 准备另一个用户的统计
        Long otherUserId = randomLongId();
        LearningStatsDO otherStats = randomPojo(LearningStatsDO.class, o -> {
            o.setUserId(otherUserId);
            o.setAccuracyRate(new BigDecimal("85.00"));
            o.setMasteryLevel("EXCELLENT");
        });
        learningStatsMapper.insert(otherStats);

        // 调用
        java.util.List<LearningStatsRespVO> result = learningStatsService.getLearningStats(userId);
        // 断言
        assertEquals(3, result.size());
    }

    @Test
    public void testGetLearningSummary() {
        // mock 数据
        Long userId = randomLongId();

        // 创建不同掌握程度的统计
        LearningStatsDO excellentStats = randomPojo(LearningStatsDO.class, o -> {
            o.setUserId(userId);
            o.setKnowledgePoint("数学");
            o.setTotalCount(10);
            o.setCorrectCount(9);
            o.setWrongCount(1);
            o.setAccuracyRate(new BigDecimal("90.00"));
            o.setMasteryLevel("EXCELLENT");
        });
        learningStatsMapper.insert(excellentStats);

        LearningStatsDO weakStats = randomPojo(LearningStatsDO.class, o -> {
            o.setUserId(userId);
            o.setKnowledgePoint("英语");
            o.setTotalCount(10);
            o.setCorrectCount(3);
            o.setWrongCount(7);
            o.setAccuracyRate(new BigDecimal("30.00"));
            o.setMasteryLevel("WEAK");
        });
        learningStatsMapper.insert(weakStats);

        // 调用
        LearningSummaryRespVO result = learningStatsService.getLearningSummary(userId);
        // 断言
        assertNotNull(result);
        assertEquals(20, result.getTotalQuestions());
        assertEquals(12, result.getCorrectCount());
        assertEquals(8, result.getWrongCount());
        assertEquals(new BigDecimal("60.00"), result.getOverallAccuracyRate());
    }

    @Test
    public void testGetByMasteryLevel() {
        // mock 数据
        Long userId = randomLongId();

        // 创建不同掌握程度的统计
        for (int i = 0; i < 3; i++) {
            final int index = i;
            LearningStatsDO stats = randomPojo(LearningStatsDO.class, o -> {
                o.setUserId(userId);
                o.setMasteryLevel("EXCELLENT");
                o.setAccuracyRate(new BigDecimal(String.valueOf(90 + index)));
            });
            learningStatsMapper.insert(stats);
        }

        LearningStatsDO weakStats = randomPojo(LearningStatsDO.class, o -> {
            o.setUserId(userId);
            o.setMasteryLevel("WEAK");
            o.setAccuracyRate(new BigDecimal("35.00"));
        });
        learningStatsMapper.insert(weakStats);

        // 调用
        java.util.List<LearningStatsRespVO> result = learningStatsService.getByMasteryLevel(userId, "EXCELLENT");
        // 断言
        assertEquals(3, result.size());
    }

    @Test
    public void testGetLearningSummary_empty() {
        // 准备参数 - 没有统计数据
        Long userId = randomLongId();

        // 调用
        LearningSummaryRespVO result = learningStatsService.getLearningSummary(userId);
        // 断言
        assertNotNull(result);
        assertEquals(0, result.getTotalQuestions());
        assertEquals(BigDecimal.ZERO, result.getOverallAccuracyRate());
    }

}