package com.chuchen.chuchenpicturebackend.controller;

import com.chuchen.chuchenpicturebackend.annotation.AuthCheck;
import com.chuchen.chuchenpicturebackend.common.BaseResponse;
import com.chuchen.chuchenpicturebackend.common.ResultUtils;
import com.chuchen.chuchenpicturebackend.constant.UserConstant;
import com.chuchen.chuchenpicturebackend.exception.BusinessException;
import com.chuchen.chuchenpicturebackend.exception.ErrorCode;
import com.chuchen.chuchenpicturebackend.manager.CosManager;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author chuchen
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private CosManager cosManager;

    /**
     * 测试上传文件
     * @param multipartFile 文件流对象
     * @return    BaseResponse 路径地址
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        String filPath = String.format("/test/%s", filename);
        File file = null;
        try {
            file = File.createTempFile(filPath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filPath, file);
            // 返回可访问的地址
            return ResultUtils.success(filPath);
        } catch (Exception e) {
            log.error("file upload error = {}", e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传文件失败");
        }finally {
            if(file != null) {
                boolean delete = file.delete();
                if(!delete) {
                    log.error("file delete error = {}", filPath);
                }
            }
        }
    }

    /**
     * 测试上传下载
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/download")
    public void testUDownloadFile(String filepath, HttpServletResponse response) {
        COSObjectInputStream objectContent = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            objectContent = cosObject.getObjectContent();
            byte[] byteArray = IOUtils.toByteArray(objectContent);
            // 设置响应头（触发下载）
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入文件流
            response.getOutputStream().write(byteArray);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error = {}, filepath = {}", e.getMessage(), filepath);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载文件失败");
        }finally {
            if(objectContent != null) {
                try {
                    objectContent.close();
                } catch (IOException e) {
                    log.error("objectContent close error = {}", e.getMessage());
                }
            }
        }
    }
}
