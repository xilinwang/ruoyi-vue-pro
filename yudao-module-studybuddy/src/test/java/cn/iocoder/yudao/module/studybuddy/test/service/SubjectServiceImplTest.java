package cn.iocoder.yudao.module.studybuddy.test.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.studybuddy.controller.admin.subject.vo.*;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.subject.SubjectMapper;
import cn.iocoder.yudao.module.studybuddy.service.subject.SubjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static cn.iocoder.yudao.module.studybuddy.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SubjectServiceImpl} 的单元测试类
 *
 * @author StudyBuddy
 */
@Import(SubjectServiceImpl.class)
public class SubjectServiceImplTest extends BaseDbUnitTest {

    @Resource
    private SubjectServiceImpl subjectService;

    @Resource
    private SubjectMapper subjectMapper;

    @Test
    public void testCreateSubject_success() {
        // 准备参数
        Long userId = randomLongId();
        SubjectCreateReqVO reqVO = randomPojo(SubjectCreateReqVO.class);

        // 调用
        Long subjectId = subjectService.createSubject(reqVO, userId);
        // 断言
        assertNotNull(subjectId);
        // 校验记录的属性是否正确
        SubjectDO subjectDO = subjectMapper.selectById(subjectId);
        assertPojoEquals(reqVO, subjectDO, "id");
        assertEquals(userId, subjectDO.getUserId());
    }

    @Test
    public void testCreateSubject_nameExists() {
        // 先创建一个科目
        Long userId = randomLongId();
        SubjectDO existingSubject = randomPojo(SubjectDO.class, o -> {
            o.setUserId(userId);
            o.setName("数学");
        });
        subjectMapper.insert(existingSubject);

        // 准备参数
        SubjectCreateReqVO reqVO = randomPojo(SubjectCreateReqVO.class, o -> o.setName("数学"));

        // 调用, 并断言异常
        assertServiceException(() -> subjectService.createSubject(reqVO, userId), SUBJECT_NAME_EXISTS);
    }

    @Test
    public void testUpdateSubject_success() {
        // mock 数据
        Long userId = randomLongId();
        SubjectDO dbSubject = randomPojo(SubjectDO.class, o -> o.setUserId(userId));
        subjectMapper.insert(dbSubject);

        // 准备参数
        SubjectUpdateReqVO reqVO = randomPojo(SubjectUpdateReqVO.class, o -> {
            o.setId(dbSubject.getId());
        });

        // 调用
        subjectService.updateSubject(reqVO);
        // 校验是否更新正确
        SubjectDO subjectDO = subjectMapper.selectById(reqVO.getId());
        assertPojoEquals(reqVO, subjectDO);
        assertEquals(userId, subjectDO.getUserId());
    }

    @Test
    public void testUpdateSubject_nameExists() {
        // mock 数据
        Long userId = randomLongId();
        SubjectDO subject1 = randomPojo(SubjectDO.class, o -> {
            o.setUserId(userId);
            o.setName("数学");
        });
        subjectMapper.insert(subject1);

        SubjectDO subject2 = randomPojo(SubjectDO.class, o -> {
            o.setUserId(userId);
            o.setName("英语");
        });
        subjectMapper.insert(subject2);

        // 准备参数 - 尝试将 subject2 的名字改为"数学"
        SubjectUpdateReqVO reqVO = randomPojo(SubjectUpdateReqVO.class, o -> {
            o.setId(subject2.getId());
            o.setName("数学");
        });

        // 调用, 并断言异常
        assertServiceException(() -> subjectService.updateSubject(reqVO), SUBJECT_NAME_EXISTS);
    }

    @Test
    public void testDeleteSubject_success() {
        // mock 数据
        SubjectDO dbSubject = randomPojo(SubjectDO.class);
        subjectMapper.insert(dbSubject);

        // 准备参数
        Long id = dbSubject.getId();

        // 调用
        subjectService.deleteSubject(id);
        // 校验数据不存在了
        assertNull(subjectMapper.selectById(id));
    }

    @Test
    public void testDeleteSubject_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> subjectService.deleteSubject(id), SUBJECT_NOT_EXISTS);
    }

    @Test
    public void testGetSubject() {
        // mock 数据
        SubjectDO dbSubject = randomPojo(SubjectDO.class);
        subjectMapper.insert(dbSubject);

        // 调用
        SubjectDO result = subjectService.getSubject(dbSubject.getId());
        // 断言
        assertNotNull(result);
        assertPojoEquals(dbSubject, result);
    }

    @Test
    public void testGetSubjectPage() {
        // mock 数据
        Long userId = randomLongId();
        for (int i = 0; i < 3; i++) {
            SubjectDO subject = randomPojo(SubjectDO.class, o -> o.setUserId(userId));
            subjectMapper.insert(subject);
        }

        // 准备参数
        SubjectPageReqVO reqVO = new SubjectPageReqVO();
        reqVO.setUserId(userId);

        // 调用
        PageResult<SubjectDO> result = subjectService.getSubjectPage(reqVO);
        // 断言
        assertEquals(3, result.getTotal());
    }

    @Test
    public void testGetSubjectListByUserId() {
        // mock 数据
        Long userId = randomLongId();
        for (int i = 0; i < 3; i++) {
            SubjectDO subject = randomPojo(SubjectDO.class, o -> o.setUserId(userId));
            subjectMapper.insert(subject);
        }

        // 准备另一个用户的科目
        Long otherUserId = randomLongId();
        SubjectDO otherSubject = randomPojo(SubjectDO.class, o -> o.setUserId(otherUserId));
        subjectMapper.insert(otherSubject);

        // 调用
        java.util.List<SubjectDO> result = subjectService.getSubjectListByUserId(userId);
        // 断言
        assertEquals(3, result.size());
    }

}