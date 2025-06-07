package com.chuchen.chuchenpicturebackend.constant;

/**
 * @author chuchen
 */
public interface CacheKeyConstant {
    /**
     * redis 缓存 key 前缀
     */
    String REDIS_CACHE_KEY_PREFIX = "chuchenpictuire:listPictureVOByPage:";

    /**
     * caffeine 缓存 key 前缀
     */
    String CAFFEINE_CACHE_KEY_PREFIX = "listPictureVOByPage:";
}
