package com.chuchen.chuchenpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chuchen.chuchenpicturebackend.model.dto.user.UserQueryRequest;
import com.chuchen.chuchenpicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chuchen.chuchenpicturebackend.model.vo.LoginUserVO;
import com.chuchen.chuchenpicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author chuchen
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-05-30 13:37:12
*/
public interface UserService extends IService<User> {

    /**
     * 获取脱敏后的用户信息
     * @param user 用户
     * @return 脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏后的用户信息（这是管理员操作的）
     * @param user 用户
     * @return 用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户信息列表（这是管理员操作的）
     * @param userList 用户列表
     * @return 用户信息列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 返回的用户 ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      请求
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取加密后的密码
     * @param password 用户密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String password);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 是否成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取当登录用户
     * @param request 请求
     * @return 登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 是否为管理员
     *
     * @param user 用户
     * @return 是否为管理员
     */
    boolean isAdmin(User user);
}
