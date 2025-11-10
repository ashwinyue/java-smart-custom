package com.smartcustom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 
 * @author SmartCustom Team
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final SmartCustomProperties properties;
    
    @Autowired
    public WebConfig(SmartCustomProperties properties) {
        this.properties = properties;
    }
    
    /**
     * 配置CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        SmartCustomProperties.Api.Cors cors = properties.getApi().getCors();
        
        registry.addMapping("/api/**")
                .allowedOrigins(cors.getAllowedOrigins())
                .allowedMethods(cors.getAllowedMethods())
                .allowedHeaders(cors.getAllowedHeaders())
                .allowCredentials(cors.isAllowCredentials());
    }
}