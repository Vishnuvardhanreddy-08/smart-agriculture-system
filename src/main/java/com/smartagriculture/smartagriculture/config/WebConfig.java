package com.smartagriculture.smartagriculture.config;

import com.smartagriculture.smartagriculture.security.FarmerAccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig registers our FarmerAccessInterceptor, so it runs before every /api controller method.
 */
// @Configuration tells Spring this class contains settings.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FarmerAccessInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**"); // register/login have no farmerId to check
    }
}