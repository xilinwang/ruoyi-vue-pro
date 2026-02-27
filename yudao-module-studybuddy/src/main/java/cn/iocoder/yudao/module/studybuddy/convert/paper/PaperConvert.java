package cn.iocoder.yudao.module.studybuddy.convert.paper;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.PaperRespVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.QuestionRespVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperDO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.QuestionDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 试卷 Convert
 *
 * @author StudyBuddy
 */
@Mapper
public interface PaperConvert {

    PaperConvert INSTANCE = Mappers.getMapper(PaperConvert.class);

    /**
     * DO 转 Response VO
     */
    PaperRespVO convert(PaperDO bean);

    /**
     * DO 列表转 Response VO 列表
     */
    List<PaperRespVO> convertList(List<PaperDO> list);

    /**
     * 分页结果转换
     */
    PageResult<PaperRespVO> convertPage(PageResult<PaperDO> page);

    /**
     * 题目 DO 列表转 Response VO 列表
     */
    List<QuestionRespVO> convertQuestionList(List<QuestionDO> list);

}
