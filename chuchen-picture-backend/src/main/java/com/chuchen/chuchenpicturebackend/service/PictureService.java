package com.chuchen.chuchenpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.chuchenpicturebackend.model.dto.picture.PictureQueryRequest;
import com.chuchen.chuchenpicturebackend.model.dto.picture.PictureUploadRequest;
import com.chuchen.chuchenpicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chuchen.chuchenpicturebackend.model.entity.User;
import com.chuchen.chuchenpicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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
     * 上传图片
     *
     * @param multipartFile 要上传的图片
     * @param pictureUploadRequest 图片上传请的额外信息
     * @param loginUser 当前用户
     * @return 图片
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

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
}
