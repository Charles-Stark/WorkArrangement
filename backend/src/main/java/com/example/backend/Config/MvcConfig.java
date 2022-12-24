package com.example.backend.Config;

import com.example.backend.Interceptor.CorsInterceptor;
import com.example.backend.Interceptor.LoginInterceptor;
import com.example.backend.Interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executors;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        // url不区分大小写
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 跨域拦截器
        registry.addInterceptor(new CorsInterceptor()).addPathPatterns("/**");

        // 登陆拦截器，拦截未登陆（无Token或Token无效）用户
        // 暂时注释以便调试
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns("/api/user/register", "/api/user/login/password");

        // 用户权限拦截器（员工/管理员）
        // 暂时注释以便调试
//        registry.addInterceptor(new UserInterceptor())
//                .excludePathPatterns("/**")
//                .addPathPatterns();

        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(3)));
        configurer.setDefaultTimeout(30000);
    }
}
