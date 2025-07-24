package org.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EncodingUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(EncodingUtils.class);
    
    /**
     * Проверяет и исправляет кодировку строки
     */
    public static String fixEncoding(String text) {
        if (text == null) {
            return null;
        }
        
        // Проверяем, есть ли проблемы с кодировкой
        if (text.contains("â") || text.contains("Ð") || text.contains("Ñ")) {
            logger.warn("Detected encoding issues in text: {}", text);
            
            try {
                // Пытаемся исправить кодировку
                byte[] bytes = text.getBytes("ISO-8859-1");
                String fixed = new String(bytes, StandardCharsets.UTF_8);
                logger.info("Fixed encoding: {} -> {}", text, fixed);
                return fixed;
            } catch (Exception e) {
                logger.error("Failed to fix encoding: {}", e.getMessage());
                return text;
            }
        }
        
        return text;
    }
    
    /**
     * Проверяет текущие настройки кодировки
     */
    public static void logEncodingInfo() {
        logger.info("=== Encoding Information ===");
        logger.info("file.encoding: {}", System.getProperty("file.encoding"));
        logger.info("sun.jnu.encoding: {}", System.getProperty("sun.jnu.encoding"));
        logger.info("Default charset: {}", Charset.defaultCharset());
        logger.info("UTF-8 available: {}", StandardCharsets.UTF_8);
        logger.info("=============================");
    }
} 