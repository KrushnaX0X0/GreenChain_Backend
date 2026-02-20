package com.krish.AgariBackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS is already configured in SecurityConfig.java
// This file is kept for reference only
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS configuration is handled by SecurityConfig.corsConfigurationSource()
}