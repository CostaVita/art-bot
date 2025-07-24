package org.example;

import org.example.util.EncodingUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EncodingTest {

    @Test
    void testEncodingFix() {
        // Тестируем исправление кодировки
        String brokenText = "âœ" + " Ваше сообщение отправлено администратору!";
        String fixedText = EncodingUtils.fixEncoding(brokenText);
        
        assertNotNull(fixedText);
        assertNotEquals(brokenText, fixedText);
        
        // Проверяем, что исправленный текст не содержит проблемных символов
        assertFalse(fixedText.contains("â"));
        assertFalse(fixedText.contains("œ"));
    }

    @Test
    void testNormalText() {
        // Тестируем нормальный текст
        String normalText = "✅ Ваше сообщение отправлено администратору!";
        String result = EncodingUtils.fixEncoding(normalText);
        
        assertEquals(normalText, result);
    }

    @Test
    void testNullText() {
        // Тестируем null
        String result = EncodingUtils.fixEncoding(null);
        assertNull(result);
    }

    @Test
    void testEmptyText() {
        // Тестируем пустую строку
        String result = EncodingUtils.fixEncoding("");
        assertEquals("", result);
    }
} 