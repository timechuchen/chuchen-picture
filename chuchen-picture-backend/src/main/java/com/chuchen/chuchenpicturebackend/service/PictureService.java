package com.chuchen.chuchenpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.chuchenpicturebackend.api.aliyunai.mocel.CreateOutPaintingTaskResponse;
import com.chuchen.chuchenpicturebackend.model.dto.picture.*;
import com.chuchen.chuchenpicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chuchen.chuchenpicturebackend.model.entity.User;
import com.chuchen.chuchenpicturebackend.model.vo.PictureVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author chuchen
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-05-31 19:53:38
*/
public interface PictureService extends IService<Picture> {

    /**
     * 校验图片
     * @param picture 图片
     */
    void validPicture(Picture picture);

    /**
     * 删除图片
     * @param pictureId 图片id
     * @param loginUser 当前用户
     */
    void deletePicture(long pictureId, User loginUser);

    /**
     * 上传图片
     *
     * @param inputSource 要上传的图片输入源（本地文件或者 url 地址）
     * @param pictureUploadRequest 图片上传请的额外信息
     * @param loginUser 当前用户
     * @return 图片
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 编辑图片
     * @param pictureEditRequest 图片编辑请求
     * @param loginUser 当前登录用户
     */
    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    /**
     * 获取查询对象
     * @param pictureQueryRequest 查询对象
     * @return QueryWrapper
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 数据脱敏（单条）
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 数据脱敏（分页）
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 获取图片分页数据（带缓存）
     */
    Page<PictureVO> getPictureVOPageWithCache(PictureQueryRequest pictureQueryRequest, HttpServletRequest request);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest 图片审核请求
     * @param loginUser 当前用户
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 填充审核参数
     * @param picture 图片
     * @param loginUser 当前用户
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量上传图片
     * @param pictureUploadByBatchRequest 图片上传请求
     * @param loginUser 当前用户
     * @return 成功的图片数量
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    /**
     * 删除图片-这里指永久删除，而且也同时删除对象存储服务器中的数据
     * @param oldPicture 删除图片的地址
     */
    void clearPictureFile(Picture oldPicture);

    /**
     * 检验空间图片的权限
     * @param loginUser 当前用户
     * @param picture 图片
     */
    void checkPictureAuth(User loginUser, Picture picture);

    /**
     * 获取空间图片列表
     * @param id 空间id
     * @return 图片列表
     */
    List<Picture> getPictureListBySpaceId(long id);

    /**
     * 搜索图片-根据色调搜索指定空间的照片
     * @param spaceId 空间id
     * @param color 颜色
     * @param loginUser 当前用户
     * @return 图片列表
     */
    List<PictureVO> searchPictureByColor(Long spaceId, String color, User loginUser);

    /**
     * 批量编辑图片
     * @param pictureEditByBatchRequest 图片批量编辑请求
     * @param loginUser 当前用户
     */
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    /**
     * AI 提交拓图任务
     */
    CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);
}
