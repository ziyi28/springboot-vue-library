package com.library.config;

import com.library.service.impl.SimpleUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 简化的Spring Security配置
 * 确保登录接口可以正常访问
 */
// @Configuration
// @EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true)
public class SimpleSecurityConfig extends WebSecurityConfigurerAdapter {

    // @Autowired
    // private SimpleUserDetailsServiceImpl userDetailsService;

  
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // @Override
    // public void configure(AuthenticationManagerBuilder auth) throws Exception {
    //     auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    // }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            // 禁用session支持 - 完全无状态
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 配置请求授权 - 完全开放所有路径
            .authorizeRequests()
            .antMatchers("/**").permitAll()
            .anyRequest().permitAll()
            .and()
            // 禁用所有安全相关功能
            .headers().frameOptions().disable()
            .and()
            // 禁用默认登录页面
            .formLogin().disable()
            .logout().disable()
            .httpBasic().disable();

        System.out.println("🔐 完全禁用Spring Security - 所有请求无认证");
    }
}