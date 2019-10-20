package com.fyp.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/static/js/**").addResourceLocations("classpath:/static/js/");
//        registry.addResourceHandler("/static/css/**").addResourceLocations("classpath:/static/css/");
//        registry.addResourceHandler("/static/fonts/**").addResourceLocations("classpath:/static/fonts/");
//        registry.addResourceHandler("/static/images/**").addResourceLocations("classpath:/static/images/");
    }
}
