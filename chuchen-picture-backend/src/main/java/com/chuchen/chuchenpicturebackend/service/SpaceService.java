package com.chuchen.chuchenpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.chuchenpicturebackend.model.dto.space.SpaceAddRequest;
import com.chuchen.chuchenpicturebackend.model.dto.space.SpaceQueryRequest;
import com.chuchen.chuchenpicturebackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chuchen.chuchenpicturebackend.model.entity.User;
import com.chuchen.chuchenpicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author chuchen
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-06-11 17:46:07
*/
public interface SpaceService extends IService<Space> {

    /**
     * 创建空间
     * @param space 需要创建的空间的信息
     * @param loginUser 当前用户
     * @return 创建成功的空间 ID
     */
    long addSpace(SpaceAddRequest space, User  loginUser);

    /**
     * 校验空间
     * @param space 空间
     * @param add 是否为新增操作
     */
    void validSpace(Space space, boolean add);

    /**
     * 数据脱敏（单条）
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 数据脱敏（分页）
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 获取查询对象
     * @param spaceQueryRequest 查询对象
     * @return QueryWrapper
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 根据空间等级填充空间对象
     * @param space 空间
     */
    void fillSpaceBySpaceLevel(Space space);
}
