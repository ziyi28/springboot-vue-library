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
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
            // 启用session支持
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .and()
            // 配置请求授权
            .authorizeRequests()
            // 允许所有路径访问 - 暂时完全开放
            .antMatchers("/**").permitAll()
            .and()
            // 禁用frame options以支持H2控制台
            .headers().frameOptions().disable();

        System.out.println("🔐 简化Spring Security配置加载完成 - 所有接口已开放");
    }
}