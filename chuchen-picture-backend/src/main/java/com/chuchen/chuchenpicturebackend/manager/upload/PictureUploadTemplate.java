package com.chuchen.chuchenpicturebackend.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.chuchen.chuchenpicturebackend.config.CosClientConfig;
import com.chuchen.chuchenpicturebackend.exception.BusinessException;
import com.chuchen.chuchenpicturebackend.exception.ErrorCode;
import com.chuchen.chuchenpicturebackend.manager.CosManager;
import com.chuchen.chuchenpicturebackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 图片上传模板
 */
@Slf4j
public abstract class PictureUploadTemplate {
  
    @Resource
    private CosClientConfig cosClientConfig;

    @Resource  
    private CosManager cosManager;

    /**
     * 处理文件来源（本地文件或者 URL）
     */
    protected abstract void processFile(Object inputSource, File file) throws IOException;

    /**
     * 获取原文件名
     */
    protected abstract String getOriginalFilename(Object inputSource);

    /**
     * 校验图片
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 上传图片
     *
     * @param inputSource    文件
     * @param uploadPathPrefix 上传路径前缀
     */
    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 校验图片
        validPicture(inputSource);
        // 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginalFilename(inputSource);
        // 自己拼接文件名，不使用原文件名，增加安全性
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        File file = null;
        try {
            // 创建临时文件，获取文件到服务器
            file = File.createTempFile(uploadPath, null);
            // 处理文件来源
            processFile(inputSource, file);
            // 上传图片到对象存储
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 获取图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 获取到图片处理结果
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if(CollUtil.isNotEmpty(objectList)) {
                // 获取压缩之后得到的文件信息
                CIObject compressCiObject = objectList.get(0);
                // 获取缩略图文件信息
                CIObject thumbnailObject = compressCiObject;
                if(objectList.size() > 1){
                    thumbnailObject = objectList.get(1);
                }
                // 封装压缩后的返回结果
                return buildResult(originalFilename, compressCiObject, thumbnailObject);
            }
            // 封装返回结果
            return buildResult(imageInfo, originalFilename, file, uploadPath);
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            this.deleteTempFile(file);
        }
    }

    /**
     * 处理图片上传结果
     * @param originalFilename 原始文件名
     * @param compressCiObject 压缩之后的文件信息
     * @param thumbnailObject 缩略图文件信息
     * @return 压缩后的结果
     */
    private UploadPictureResult buildResult(String originalFilename, CIObject compressCiObject, CIObject thumbnailObject) {
        // 封装返回结果
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = compressCiObject.getWidth();
        int picHeight = compressCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressCiObject.getFormat());
        uploadPictureResult.setPicSize(compressCiObject.getSize().longValue());
        // 设置压缩后的原图地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressCiObject.getKey());
        // 缩略图地址
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailObject.getKey());
        return uploadPictureResult;
    }

    /**
     * 处理图片上传结果
     * @param imageInfo 对象存储返回的信息
     * @param originFilename 原文件名
     * @param file 文件
     * @param uploadPath 上传路径
     * @return 上传结果
     */
    private UploadPictureResult buildResult(ImageInfo imageInfo, String originFilename, File file, String uploadPath) {
        // 封装返回结果
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        return uploadPictureResult;
    }

    /**
     * 删除临时文件
     */
    private void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }
}
