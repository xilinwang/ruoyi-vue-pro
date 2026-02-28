package cn.iocoder.yudao.module.studybuddy.service.subject;

import cn.iocoder.yudao.module.studybuddy.controller.admin.subject.vo.SubjectCreateReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.subject.vo.SubjectUpdateReqVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.studybuddy.dal.mysql.subject.SubjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * SubjectService 单元测试
 *
 * @author StudyBuddy
 */
@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectMapper subjectMapper;

    @InjectMocks
    private SubjectServiceImpl subjectService;

    private SubjectDO testSubject;

    @BeforeEach
    void setUp() {
        testSubject = SubjectDO.builder()
                .id(1L)
                .userId(1L)
                .name("数学")
                .description("高中数学课程")
                .build();
    }

    @Test
    void testCreateSubject_Success() {
        // Arrange
        SubjectCreateReqVO createReqVO = new SubjectCreateReqVO();
        createReqVO.setName("物理");
        createReqVO.setDescription("高中物理课程");

        when(subjectMapper.selectByUserIdAndName(anyLong(), anyString())).thenReturn(null);
        when(subjectMapper.insert(any(SubjectDO.class))).thenAnswer(invocation -> {
            SubjectDO subject = invocation.getArgument(0);
            subject.setId(2L);
            return subject;
        });

        // Act
        Long result = subjectService.createSubject(createReqVO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result);
        verify(subjectMapper, times(1)).selectByUserIdAndName(1L, "物理");
        verify(subjectMapper, times(1)).insert(any(SubjectDO.class));
    }

    @Test
    void testCreateSubject_NameExists() {
        // Arrange
        SubjectCreateReqVO createReqVO = new SubjectCreateReqVO();
        createReqVO.setName("数学");
        createReqVO.setDescription("高中数学课程");

        when(subjectMapper.selectByUserIdAndName(anyLong(), anyString())).thenReturn(testSubject);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            subjectService.createSubject(createReqVO, 1L);
        });

        verify(subjectMapper, times(1)).selectByUserIdAndName(1L, "数学");
        verify(subjectMapper, never()).insert(any(SubjectDO.class));
    }

    @Test
    void testUpdateSubject_Success() {
        // Arrange
        SubjectUpdateReqVO updateReqVO = new SubjectUpdateReqVO();
        updateReqVO.setId(1L);
        updateReqVO.setName("高级数学");
        updateReqVO.setDescription("高中高级数学课程");

        when(subjectMapper.selectById(1L)).thenReturn(testSubject);
        when(subjectMapper.selectByUserIdAndName(anyLong(), anyString())).thenReturn(null);
        doNothing().when(subjectMapper).updateById(any(SubjectDO.class));

        // Act
        subjectService.updateSubject(updateReqVO);

        // Assert
        verify(subjectMapper, times(1)).selectById(1L);
        verify(subjectMapper, times(1)).selectByUserIdAndName(1L, "高级数学");
        verify(subjectMapper, times(1)).updateById(any(SubjectDO.class));
    }

    @Test
    void testUpdateSubject_NotFound() {
        // Arrange
        SubjectUpdateReqVO updateReqVO = new SubjectUpdateReqVO();
        updateReqVO.setId(999L);
        updateReqVO.setName("物理");

        when(subjectMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            subjectService.updateSubject(updateReqVO);
        });

        verify(subjectMapper, times(1)).selectById(999L);
        verify(subjectMapper, never()).updateById(any(SubjectDO.class));
    }

    @Test
    void testDeleteSubject_Success() {
        // Arrange
        when(subjectMapper.selectById(1L)).thenReturn(testSubject);
        doNothing().when(subjectMapper).deleteById(1L);

        // Act
        subjectService.deleteSubject(1L);

        // Assert
        verify(subjectMapper, times(1)).selectById(1L);
        verify(subjectMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteSubject_NotFound() {
        // Arrange
        when(subjectMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            subjectService.deleteSubject(999L);
        });

        verify(subjectMapper, times(1)).selectById(999L);
        verify(subjectMapper, never()).deleteById(anyLong());
    }

    @Test
    void testGetSubject() {
        // Arrange
        when(subjectMapper.selectById(1L)).thenReturn(testSubject);

        // Act
        SubjectDO result = subjectService.getSubject(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("数学", result.getName());
        verify(subjectMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetSubject_NotFound() {
        // Arrange
        when(subjectMapper.selectById(999L)).thenReturn(null);

        // Act
        SubjectDO result = subjectService.getSubject(999L);

        // Assert
        assertNull(result);
        verify(subjectMapper, times(1)).selectById(999L);
    }
}
