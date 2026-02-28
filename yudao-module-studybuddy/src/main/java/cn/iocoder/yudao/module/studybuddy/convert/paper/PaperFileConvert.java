package cn.iocoder.yudao.module.studybuddy.convert.paper;

import cn.iocoder.yudao.module.studybuddy.controller.admin.paper.vo.PaperFileRespVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.paper.PaperFileDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 试卷文件 Convert
 *
 * @author StudyBuddy
 */
@Mapper
public interface PaperFileConvert {

    PaperFileConvert INSTANCE = Mappers.getMapper(PaperFileConvert.class);

    PaperFileRespVO convert(PaperFileDO bean);

    List<PaperFileRespVO> convertList(List<PaperFileDO> list);

}
