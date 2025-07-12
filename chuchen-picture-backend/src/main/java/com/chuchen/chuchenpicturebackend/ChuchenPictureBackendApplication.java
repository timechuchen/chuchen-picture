package com.chuchen.chuchenpicturebackend;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class}) // 排除shardingSphere的自动配置
@MapperScan("com.chuchen.chuchenpicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true) // 开启AOP代理
@EnableAsync
public class ChuchenPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChuchenPictureBackendApplication.class, args);
    }

}
