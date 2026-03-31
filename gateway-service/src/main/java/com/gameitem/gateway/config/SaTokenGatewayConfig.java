package com.gameitem.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaTokenGatewayConfig {

    @Bean
    public SaReactorFilter saReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                // 放行：注册/登录接口（以及 public health 等可匿名访问）
                .addExclude(
                        "/api/user/auth/**",
                        "/api/user/public/**",
                        "/actuator/**"
                )
                .setAuth(obj -> {
                    // 跨域预检请求会发 OPTIONS；若鉴权不放行会导致前端 fetch 失败
                    try {
                        String method = SaHolder.getRequest().getMethod();
                        if (method != null && "OPTIONS".equalsIgnoreCase(method)) {
                            return;
                        }
                    } catch (Exception ignore) {
                        // 若取不到 method，按默认鉴权策略执行
                    }
                    StpUtil.checkLogin();
                });
    }
}
