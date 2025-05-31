package com.chuchen.chuchenpicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chuchen
 * @description 用户注册参数模型
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 6957288647358928978L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
