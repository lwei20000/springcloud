package com.com.itcast.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 *初始版本 eureka 安全验证放行
 *
 * 注意：本类路径要注意，否则会导致服务注册不上
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // http.csrf().disable();

        // 忽略掉/eureka/**
        http.csrf().ignoringAntMatchers("/eureka/**");

        super.configure(http);
    }
}
