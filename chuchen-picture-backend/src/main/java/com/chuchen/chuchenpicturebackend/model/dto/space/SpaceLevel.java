package com.chuchen.chuchenpicturebackend.model.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author chuchen
 * @description 空间级别
 */
@Data
@AllArgsConstructor
public class SpaceLevel {

    /**
     * 空间级别值
     */
    private int value;

    /**
     * 空间级别描述
     */
    private String text;

    /**
     * 最大数量
     */
    private long maxCount;

    /**
     * 最大容量
     */
    private long maxSize;
}
