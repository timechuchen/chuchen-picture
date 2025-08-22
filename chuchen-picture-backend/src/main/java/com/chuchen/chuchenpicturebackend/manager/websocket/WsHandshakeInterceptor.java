package com.chuchen.chuchenpicturebackend.manager.websocket;

import cn.hutool.core.util.StrUtil;
import com.chuchen.chuchenpicturebackend.manager.auth.SpaceUserAuthManager;
import com.chuchen.chuchenpicturebackend.manager.auth.model.SpaceUserPermissionConstant;
import com.chuchen.chuchenpicturebackend.model.entity.Picture;
import com.chuchen.chuchenpicturebackend.model.entity.Space;
import com.chuchen.chuchenpicturebackend.model.entity.User;
import com.chuchen.chuchenpicturebackend.model.enums.SpaceTypeEnum;
import com.chuchen.chuchenpicturebackend.service.PictureService;
import com.chuchen.chuchenpicturebackend.service.SpaceService;
import com.chuchen.chuchenpicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author chuchen
 * <br/>
 * WebSocket 握手拦截器，建立连接前要校验
 */
@Slf4j
@Component
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private UserService userService;
    @Resource
    private PictureService pictureService;
    @Resource
    private SpaceService spaceService;
    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    /**
     * 建立连接前需要先校验
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 获取当前用户
        if(request instanceof ServletServerHttpRequest) {
            HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            // 从请求中获取参数
            String pictureId = httpServletRequest.getParameter("pictureId");
            if(StrUtil.isBlank(pictureId)) {
                log.error("缺少图片参数，拒绝握手");
                return false;
            }
            User loginUser = userService.getLoginUser(httpServletRequest);
            if(loginUser == null) {
                log.error("用户未登录，拒绝握手");
                return false;
            }
            // 校验当前用户是否有编辑当前图片的权限（如果是团队空间并且有编辑权限才能进行建立连接）
            Picture picture = pictureService.getById(pictureId);
            if(Objects.isNull(picture)) {
                log.error("图片不存在，拒绝握手");
                return false;
            }
            Long spaceId = picture.getSpaceId();
            Space space = null;
            if(spaceId != null) {
                space = spaceService.getById(spaceId);
                if(Objects.isNull(space)) {
                    log.error("空间不存在，拒绝握手");
                    return false;
                }
                if(space.getSpaceType() != SpaceTypeEnum.TEAM.getValue()) {
                    log.error("非团队空间，拒绝握手");
                    return false;
                }
            }
            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
            if(!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)) {
                log.error("用户不存在编辑权限，拒绝握手");
                return false;
            }

            // 设置当前用户到 WebSocket 会话中
            attributes.put("user", loginUser);
            attributes.put("userId", loginUser.getId());
            attributes.put("pictureId", Long.valueOf(pictureId));  // 需要转化为 Long 类型
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
