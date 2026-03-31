package com.zhituan.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源进行跨域测试，开发环境适用
        config.addAllowedOriginPattern("*"); 
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法(GET, POST, PUT, DELETE等)
        config.addAllowedMethod("*");
        // 是否允许携带Cookie等凭证
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
