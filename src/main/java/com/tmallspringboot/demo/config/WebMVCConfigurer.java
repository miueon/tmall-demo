package com.tmallspringboot.demo.config;

import com.tmallspringboot.demo.interceptor.AddonInterceptor;
import com.tmallspringboot.demo.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfigurer implements WebMvcConfigurer {
    @Bean
    public LoginInterceptor getLoginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean
    public AddonInterceptor getAddonInterceptor() {
        return new AddonInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLoginInterceptor())
                .addPathPatterns("/**");
        registry.addInterceptor(getAddonInterceptor())
                .addPathPatterns("/**");
    }
}
