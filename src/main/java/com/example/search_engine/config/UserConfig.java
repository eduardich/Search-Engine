package com.example.search_engine.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("user-config")
@Data
public class UserConfig {

    private List<SiteAttributes> siteAttributes;

    private String webInterfacePath;

    private Map<String,String> jsoupConnectionProperties;

}
