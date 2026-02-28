package cn.iocoder.yudao.module.studybuddy.service.stats;

import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.LearningStatsRespVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.LearningSummaryRespVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.LearningStatsDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.stats.LearningStatsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学习统计 Service 实现类
 *
 * @author StudyBuddy
 */
@Service
@Validated
@Slf4j
public class LearningStatsServiceImpl implements LearningStatsService {

    private static final BigDecimal EXCELLENT_THRESHOLD = new BigDecimal("90");
    private static final BigDecimal GOOD_THRESHOLD = new BigDecimal("70");
    private static final BigDecimal NORMAL_THRESHOLD = new BigDecimal("50");

    @Resource
    private LearningStatsMapper learningStatsMapper;

    @Override
    public List<LearningStatsRespVO> getLearningStats(Long userId) {
        List<LearningStatsDO> statsList = learningStatsMapper.selectByUserId(userId);
        return statsList.stream()
                .map(this::convertToRespVO)
                .sorted(Comparator.comparing(LearningStatsRespVO::getAccuracyRate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public LearningSummaryRespVO getLearningSummary(Long userId) {
        List<LearningStatsDO> statsList = learningStatsMapper.selectByUserId(userId);

        LearningSummaryRespVO summary = new LearningSummaryRespVO();

        // 计算总体统计
        int totalQuestions = statsList.stream().mapToInt(s -> s.getTotalCount() != null ? s.getTotalCount() : 0).sum();
        int correctCount = statsList.stream().mapToInt(s -> s.getCorrectCount() != null ? s.getCorrectCount() : 0).sum();
        int wrongCount = statsList.stream().mapToInt(s -> s.getWrongCount() != null ? s.getWrongCount() : 0).sum();

        summary.setTotalQuestions(totalQuestions);
        summary.setCorrectCount(correctCount);
        summary.setWrongCount(wrongCount);

        if (totalQuestions > 0) {
            BigDecimal overallRate = new BigDecimal(correctCount)
                    .multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(totalQuestions), 2, RoundingMode.HALF_UP);
            summary.setOverallAccuracyRate(overallRate);
        } else {
            summary.setOverallAccuracyRate(BigDecimal.ZERO);
        }

        // 分类知识点
        List<LearningSummaryRespVO.KnowledgePointStats> strongPoints = new ArrayList<>();
        List<LearningSummaryRespVO.KnowledgePointStats> weakPoints = new ArrayList<>();

        for (LearningStatsDO stats : statsList) {
            LearningSummaryRespVO.KnowledgePointStats kpStats = new LearningSummaryRespVO.KnowledgePointStats();
            kpStats.setKnowledgePoint(stats.getKnowledgePoint());
            kpStats.setTotalCount(stats.getTotalCount());
            kpStats.setCorrectCount(stats.getCorrectCount());
            kpStats.setAccuracyRate(stats.getAccuracyRate());
            kpStats.setMasteryLevel(stats.getMasteryLevel());

            String masteryLevel = stats.getMasteryLevel();
            if ("EXCELLENT".equals(masteryLevel) || "GOOD".equals(masteryLevel)) {
                strongPoints.add(kpStats);
            } else if ("WEAK".equals(masteryLevel)) {
                weakPoints.add(kpStats);
            }
        }

        // 按正确率排序
        strongPoints.sort(Comparator.comparing(LearningSummaryRespVO.KnowledgePointStats::getAccuracyRate).reversed());
        weakPoints.sort(Comparator.comparing(LearningSummaryRespVO.KnowledgePointStats::getAccuracyRate));

        // 限制数量
        summary.setStrongPoints(strongPoints.stream().limit(5).collect(Collectors.toList()));
        summary.setWeakPoints(weakPoints.stream().limit(5).collect(Collectors.toList()));

        // 生成学习建议
        summary.setSuggestions(generateSuggestions(summary, weakPoints));

        return summary;
    }

    @Override
    public void updateLearningStats(Long userId, String knowledgePoint, boolean isCorrect) {
        LearningStatsDO stats = learningStatsMapper.selectByUserAndKnowledgePoint(userId, knowledgePoint);

        if (stats == null) {
            // 创建新记录
            stats = LearningStatsDO.builder()
                    .userId(userId)
                    .knowledgePoint(knowledgePoint)
                    .totalCount(1)
                    .correctCount(isCorrect ? 1 : 0)
                    .wrongCount(isCorrect ? 0 : 1)
                    .lastPracticeTime(LocalDateTime.now())
                    .build();
            updateAccuracyAndMastery(stats);
            learningStatsMapper.insert(stats);
        } else {
            // 更新记录
            stats.setTotalCount(stats.getTotalCount() + 1);
            if (isCorrect) {
                stats.setCorrectCount(stats.getCorrectCount() + 1);
            } else {
                stats.setWrongCount(stats.getWrongCount() + 1);
            }
            stats.setLastPracticeTime(LocalDateTime.now());
            updateAccuracyAndMastery(stats);
            learningStatsMapper.updateById(stats);
        }

        log.debug("[updateLearningStats] 更新学习统计: userId={}, knowledgePoint={}, isCorrect={}", userId, knowledgePoint, isCorrect);
    }

    @Override
    public List<LearningStatsRespVO> getByMasteryLevel(Long userId, String masteryLevel) {
        List<LearningStatsDO> statsList = learningStatsMapper.selectByMasteryLevel(userId, masteryLevel);
        return statsList.stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
    }

    private void updateAccuracyAndMastery(LearningStatsDO stats) {
        if (stats.getTotalCount() > 0) {
            BigDecimal accuracyRate = new BigDecimal(stats.getCorrectCount())
                    .multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(stats.getTotalCount()), 2, RoundingMode.HALF_UP);
            stats.setAccuracyRate(accuracyRate);

            // 设置掌握程度
            if (accuracyRate.compareTo(EXCELLENT_THRESHOLD) >= 0) {
                stats.setMasteryLevel("EXCELLENT");
            } else if (accuracyRate.compareTo(GOOD_THRESHOLD) >= 0) {
                stats.setMasteryLevel("GOOD");
            } else if (accuracyRate.compareTo(NORMAL_THRESHOLD) >= 0) {
                stats.setMasteryLevel("NORMAL");
            } else {
                stats.setMasteryLevel("WEAK");
            }
        } else {
            stats.setAccuracyRate(BigDecimal.ZERO);
            stats.setMasteryLevel("NORMAL");
        }
    }

    private List<String> generateSuggestions(LearningSummaryRespVO summary, List<LearningSummaryRespVO.KnowledgePointStats> weakPoints) {
        List<String> suggestions = new ArrayList<>();

        if (summary.getTotalQuestions() < 10) {
            suggestions.add("练习题目数量较少，建议多做练习以积累更多数据，从而获得更准确的学习分析。");
        }

        if (!weakPoints.isEmpty()) {
            suggestions.add("建议重点复习以下薄弱知识点：" +
                    weakPoints.stream()
                            .limit(3)
                            .map(LearningSummaryRespVO.KnowledgePointStats::getKnowledgePoint)
                            .collect(Collectors.joining("、")));
            suggestions.add("针对薄弱知识点，建议回顾课本相关章节，做专项练习巩固基础。");
        }

        if (summary.getOverallAccuracyRate().compareTo(new BigDecimal("60")) < 0) {
            suggestions.add("整体正确率较低，建议从基础知识开始系统复习，确保理解每个概念。");
        } else if (summary.getOverallAccuracyRate().compareTo(new BigDecimal("80")) >= 0) {
            suggestions.add("学习情况良好！建议尝试挑战更难的题目，拓展知识深度。");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("继续坚持练习，保持良好的学习习惯！");
        }

        return suggestions;
    }

    private LearningStatsRespVO convertToRespVO(LearningStatsDO stats) {
        LearningStatsRespVO respVO = new LearningStatsRespVO();
        respVO.setId(stats.getId());
        respVO.setKnowledgePoint(stats.getKnowledgePoint());
        respVO.setTotalCount(stats.getTotalCount());
        respVO.setCorrectCount(stats.getCorrectCount());
        respVO.setWrongCount(stats.getWrongCount());
        respVO.setAccuracyRate(stats.getAccuracyRate());
        respVO.setMasteryLevel(stats.getMasteryLevel());
        respVO.setMasteryLevelDesc(getMasteryLevelDesc(stats.getMasteryLevel()));
        respVO.setLastPracticeTime(stats.getLastPracticeTime());
        return respVO;
    }

    private String getMasteryLevelDesc(String masteryLevel) {
        if (masteryLevel == null) {
            return "未知";
        }
        switch (masteryLevel) {
            case "EXCELLENT":
                return "优秀";
            case "GOOD":
                return "良好";
            case "NORMAL":
                return "一般";
            case "WEAK":
                return "薄弱";
            default:
                return "未知";
        }
    }

}
