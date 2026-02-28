package cn.iocoder.yudao.module.studybuddy.service.subject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.subject.vo.*;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.subject.SubjectDO;

import javax.validation.Valid;

/**
 * 科目 Service 接口
 *
 * @author StudyBuddy
 */
public interface SubjectService {

    /**
     * 创建科目
     *
     * @param createReqVO 创建信息
     * @param userId 用户ID
     * @return 科目ID
     */
    Long createSubject(@Valid SubjectCreateReqVO createReqVO, Long userId);

    /**
     * 更新科目
     *
     * @param updateReqVO 更新信息
     */
    void updateSubject(@Valid SubjectUpdateReqVO updateReqVO);

    /**
     * 删除科目
     *
     * @param id 科目ID
     */
    void deleteSubject(Long id);

    /**
     * 获取科目详情
     *
     * @param id 科目ID
     * @return 科目详情
     */
    SubjectDO getSubject(Long id);

    /**
     * 获取科目分页
     *
     * @param pageReqVO 分页查询条件
     * @return 科目分页结果
     */
    PageResult<SubjectDO> getSubjectPage(SubjectPageReqVO pageReqVO);

    /**
     * 根据用户ID获取科目列表
     *
     * @param userId 用户ID
     * @return 科目列表
     */
    java.util.List<SubjectDO> getSubjectListByUserId(Long userId);

}
