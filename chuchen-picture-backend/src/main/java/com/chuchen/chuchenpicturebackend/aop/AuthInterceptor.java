package com.chuchen.chuchenpicturebackend.aop;

import com.chuchen.chuchenpicturebackend.annotation.AuthCheck;
import com.chuchen.chuchenpicturebackend.exception.BusinessException;
import com.chuchen.chuchenpicturebackend.exception.ErrorCode;
import com.chuchen.chuchenpicturebackend.model.entity.User;
import com.chuchen.chuchenpicturebackend.model.enums.UserRoleEnum;
import com.chuchen.chuchenpicturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author chuchen
 * @description 这是权限校验的 AOP 切面
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截（权限校验）
     * @param pjp 切入点
     * @param authCheck 权限校验注解
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint pjp, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        // 拿到当前请求的所有属性
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (requestAttributes != null) {
            request = ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        // 获取当亲登录用户
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 如果不需要权限，放行
        if(mustRoleEnum == null){
            return pjp.proceed();
        }
        // 必须有对应的权限才能通过
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if(userRoleEnum == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 要求必须有管理员权限，但是又没有，则要拒绝
        if(UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 放行
        return pjp.proceed();
    }
}
