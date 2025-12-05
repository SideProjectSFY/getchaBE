package com.ssafy.backend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiPrefixConfig implements WebMvcConfigurer {

    private static final String API_PREFIX = "/api/v1";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PREFIX, clazz ->
                clazz.isAnnotationPresent(RestController.class)
                        && clazz.getPackageName().startsWith("com.ssafy.backend"));
    }
}
