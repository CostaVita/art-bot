package org.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Configuration
public class EncodingConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(EncodingConfig.class);
    
    @PostConstruct
    public void setupEncoding() {
        logger.info("Setting up UTF-8 encoding...");
        
        // Проверяем текущую кодировку
        String fileEncoding = System.getProperty("file.encoding");
        String sunJnuEncoding = System.getProperty("sun.jnu.encoding");
        Charset defaultCharset = Charset.defaultCharset();
        
        logger.info("Current file.encoding: {}", fileEncoding);
        logger.info("Current sun.jnu.encoding: {}", sunJnuEncoding);
        logger.info("Default charset: {}", defaultCharset);
        
        // Устанавливаем UTF-8 если не установлен
        if (!StandardCharsets.UTF_8.name().equals(fileEncoding)) {
            logger.warn("file.encoding is not UTF-8, setting it...");
            System.setProperty("file.encoding", StandardCharsets.UTF_8.name());
        }
        
        if (!StandardCharsets.UTF_8.name().equals(sunJnuEncoding)) {
            logger.warn("sun.jnu.encoding is not UTF-8, setting it...");
            System.setProperty("sun.jnu.encoding", StandardCharsets.UTF_8.name());
        }
        
        logger.info("Encoding setup completed");
    }
} 