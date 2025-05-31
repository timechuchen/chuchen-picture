package com.chuchen.chuchenpicturebackend.model.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @author chuchen
 * @description: 用户角色枚举
 */
@Getter
public enum UserRoleEnum {

    USER("用户", "user"),
    ADMIN("管理员", "admin");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据值获取枚举
     * @param value 值
     * @return 枚举
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if(Objects.isNull(value)) {
            return null;
        }

        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.getValue().equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }
}
