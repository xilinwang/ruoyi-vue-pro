package cn.iocoder.yudao.module.studybuddy.service.stats;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookExportReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookPageReqVO;
import cn.iocoder.yudao.module.studybuddy.controller.admin.stats.vo.WrongBookRespVO;
import cn.iocoder.yudao.module.studybuddy.dal.dataobject.stats.WrongBookDO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 错题本 Service 接口
 *
 * @author StudyBuddy
 */
public interface WrongBookService {

    /**
     * 获取错题本分页
     *
     * @param userId 用户ID
     * @param reqVO  分页请求
     * @return 分页结果
     */
    PageResult<WrongBookRespVO> getWrongBookPage(Long userId, WrongBookPageReqVO reqVO);

    /**
     * 获取错题详情
     *
     * @param userId 用户ID
     * @param id     错题ID
     * @return 错题详情
     */
    WrongBookRespVO getWrongBook(Long userId, Long id);

    /**
     * 添加错题
     *
     * @param wrongBook 错题信息
     */
    void addWrongBook(WrongBookDO wrongBook);

    /**
     * 更新错题掌握状态
     *
     * @param userId     用户ID
     * @param id         错题ID
     * @param isMastered 是否已掌握
     */
    void updateMasteredStatus(Long userId, Long id, Boolean isMastered);

    /**
     * 删除错题
     *
     * @param userId 用户ID
     * @param id     错题ID
     */
    void deleteWrongBook(Long userId, Long id);

    /**
     * 导出错题本
     *
     * @param userId  用户ID
     * @param reqVO   导出请求
     * @param response HTTP响应
     */
    void exportWrongBook(Long userId, WrongBookExportReqVO reqVO, HttpServletResponse response);

    /**
     * 获取用户错题列表
     *
     * @param userId 用户ID
     * @return 错题列表
     */
    List<WrongBookDO> getWrongBookList(Long userId);

}
