package com.example.search_engine.config;

import com.example.search_engine.services.searchLogic.LemmaFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class LemmaFinderConfig {

    @Bean
    public LemmaFinder lemmaFinder(){
        try {
            return LemmaFinder.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
