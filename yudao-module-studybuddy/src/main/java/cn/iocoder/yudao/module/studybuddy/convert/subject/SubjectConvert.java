package cn.iocoder.yudao.module.studybuddy.convert.subject;

import cn.iocoder.yudao.module.studybuddy.controller.admin.subject.vo.*;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.subject.SubjectDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 科目 Convert
 *
 * @author StudyBuddy
 */
@Mapper
public interface SubjectConvert {

    SubjectConvert INSTANCE = Mappers.getMapper(SubjectConvert.class);

    SubjectDO convert(SubjectCreateReqVO bean);

    SubjectDO convert(SubjectUpdateReqVO bean);

    SubjectRespVO convert(SubjectDO bean);

    List<SubjectRespVO> convertList(List<SubjectDO> list);

}
