package org.example.config;

import org.example.bot.AdminBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    @Value("${telegram.bot.username}")
    private String botUsername;
    
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotConfig.class);
    
    @Bean
    public AdminBot adminBot() {
        logger.info("Initializing AdminBot...");
        logger.info("Raw botToken: '{}'", botToken);
        logger.info("Raw botUsername: '{}'", botUsername);
        
        // Валидация токена
        if (botToken == null || botToken.equals("your_bot_token_here") || botToken.equals("${BOT_TOKEN:your_bot_token_here}")) {
            logger.error("BOT_TOKEN не настроен! Установите переменную окружения BOT_TOKEN или настройте telegram.bot.token в application.properties");
            throw new IllegalStateException("BOT_TOKEN не настроен");
        }
        
        // Валидация username
        if (botUsername == null || botUsername.equals("your_bot_username") || botUsername.equals("${BOT_USERNAME:your_bot_username}")) {
            logger.error("BOT_USERNAME не настроен! Установите переменную окружения BOT_USERNAME или настройте telegram.bot.username в application.properties");
            throw new IllegalStateException("BOT_USERNAME не настроен");
        }
        
        logger.info("Bot token configured: {}", botToken.substring(0, Math.min(10, botToken.length())) + "...");
        logger.info("Bot username configured: {}", botUsername);
        
        AdminBot bot = new AdminBot();
        bot.setBotToken(botToken);
        bot.setBotUsername(botUsername);
        return bot;
    }
    
    @Bean
    public TelegramBotsApi telegramBotsApi(AdminBot adminBot) throws TelegramApiException {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(adminBot);
            return api;
        } catch (TelegramApiException e) {
            if (e.getMessage().contains("Error removing old webhook") || e.getMessage().contains("404")) {
                throw new TelegramApiException("Неверный токен бота или бот не существует. Проверьте BOT_TOKEN в настройках.", e);
            }
            throw e;
        }
    }
} 