package com.chuchen.chuchenpicturebackend.api.imagesearch;

import com.chuchen.chuchenpicturebackend.api.imagesearch.model.ImageSearchResult;
import com.chuchen.chuchenpicturebackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.chuchen.chuchenpicturebackend.api.imagesearch.sub.GetImageListApi;
import com.chuchen.chuchenpicturebackend.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSearchApiFacade {

    /**
     * 搜索图片
     *
     * @param imageUrl
     * @return
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://q2.qlogo.cn/headimg_dl?spec=100&dst_uin=2665300871";
        List<ImageSearchResult> resultList = searchImage(imageUrl);
        System.out.println("结果列表" + resultList);
    }
}
