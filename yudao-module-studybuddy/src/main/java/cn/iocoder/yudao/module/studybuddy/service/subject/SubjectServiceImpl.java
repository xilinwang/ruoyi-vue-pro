package cn.iocoder.yudao.module.studybuddy.service.subject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.subject.vo.*;
import cn.iocoder.yudao.module.studybuddy.convert.subject.SubjectConvert;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.subject.SubjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.studybuddy.enums.ErrorCodeConstants.*;

/**
 * 科目 Service 实现类
 *
 * @author StudyBuddy
 */
@Service
@Validated
public class SubjectServiceImpl implements SubjectService {

    @Resource
    private SubjectMapper subjectMapper;

    @Override
    public Long createSubject(SubjectCreateReqVO createReqVO, Long userId) {
        // 校验科目名称是否已存在
        SubjectDO existing = subjectMapper.selectByUserIdAndName(userId, createReqVO.getName());
        if (existing != null) {
            exception(SUBJECT_NAME_EXISTS);
        }

        // 插入数据库
        SubjectDO subject = SubjectDO.builder()
                .userId(userId)
                .name(createReqVO.getName())
                .description(createReqVO.getDescription())
                .build();
        subjectMapper.insert(subject);
        return subject.getId();
    }

    @Override
    public void updateSubject(SubjectUpdateReqVO updateReqVO) {
        // 校验存在
        validateSubjectExists(updateReqVO.getId());

        // 校验名称是否重复
        SubjectDO existing = subjectMapper.selectById(updateReqVO.getId());
        SubjectDO nameCheck = subjectMapper.selectByUserIdAndName(existing.getUserId(), updateReqVO.getName());
        if (nameCheck != null && !nameCheck.getId().equals(updateReqVO.getId())) {
            exception(SUBJECT_NAME_EXISTS);
        }

        // 更新数据库
        SubjectDO updateObj = SubjectDO.builder()
                .id(updateReqVO.getId())
                .name(updateReqVO.getName())
                .description(updateReqVO.getDescription())
                .build();
        subjectMapper.updateById(updateObj);
    }

    @Override
    public void deleteSubject(Long id) {
        // 校验存在
        validateSubjectExists(id);
        // 删除
        subjectMapper.deleteById(id);
    }

    private SubjectDO validateSubjectExists(Long id) {
        SubjectDO subject = subjectMapper.selectById(id);
        if (subject == null) {
            exception(SUBJECT_NOT_EXISTS);
        }
        return subject;
    }

    @Override
    public SubjectDO getSubject(Long id) {
        return subjectMapper.selectById(id);
    }

    @Override
    public PageResult<SubjectDO> getSubjectPage(SubjectPageReqVO pageReqVO) {
        return subjectMapper.selectPage(pageReqVO);
    }

    @Override
    public java.util.List<SubjectDO> getSubjectListByUserId(Long userId) {
        return subjectMapper.selectListByUserId(userId);
    }

}
