package com.chuchen.chuchenpicturebackend.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author chuchen
 * @description 图片标签分类列表
 */
@Data
public class PictureTagCategory {

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 分类列表
     */
    private List<String> categoryList;
}
