package org.example.bot;

import org.example.config.BotConfig;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AdminBot extends TelegramLongPollingBot {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminBot.class);
    
    @Autowired
    private BotConfig botConfig;
    
    @Autowired
    private UserService userService;

    private String botToken;
    private String botUsername;
    
    // Временное хранение ожидающих медиа файлов от админа
    private Long pendingMediaTargetUserId = null;
    
    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
    
    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }
    
    @Override
    public String getBotToken() {
        return botToken;
    }
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        try {
            logger.info("Received update: {}", update.getUpdateId());
            
            if (update.hasMessage()) {
                Message message = update.getMessage();
                User user = message.getFrom();
                
                logger.info("Message from user {} ({}): {}", 
                    user.getId(), 
                    user.getUserName() != null ? "@" + user.getUserName() : "no username",
                    message.hasText() ? message.getText() : "non-text message"
                );
                
                // Сохраняем информацию о пользователе
                userService.addOrUpdateUser(
                    user.getId(),
                    user.getUserName(),
                    user.getFirstName(),
                    user.getLastName()
                );
                
                // Обрабатываем команды
                if (message.hasText() && message.getText().startsWith("/")) {
                    handleCommand(message);
                } else {
                    // Обрабатываем обычные сообщения
                    handleMessage(message);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing update: ", e);
        }
    }
    
    private void handleCommand(Message message) {
        String command = message.getText().split(" ")[0].toLowerCase();
        User user = message.getFrom();
        
        switch (command) {
            case "/start":
                handleStart(message);
                break;
            case "/help":
                handleHelp(message);
                break;
            case "/reply":
                if (isAdmin(user.getId())) {
                    handleAdminReply(message);
                } else {
                    sendMessage(message.getChatId(), botConfig.getMessages().getUnauthorized());
                }
                break;
            case "/users":
                if (isAdmin(user.getId())) {
                    handleUsersList(message);
                } else {
                    sendMessage(message.getChatId(), botConfig.getMessages().getUnauthorized());
                }
                break;
            default:
                sendMessage(message.getChatId(), "Неизвестная команда. Используйте /help для справки.");
        }
    }
    
    private void handleStart(Message message) {
        try {
            SendMessage welcomeMessage = new SendMessage();
            welcomeMessage.setChatId(message.getChatId().toString());
            welcomeMessage.setText("🖼️ *Добро пожаловать в персональную арт-мастерскую!*\n\nЯ создаю идеальные картины для вашего интерьера. Просто отправьте фото комнаты — и искусственный интеллект:\n\n1. 🔍 Проанализирует стиль и цветовую гамму\n2. 🎨 Сгенерирует 3 варианта картин специально под ваш интерьер\n3. 🚚 Организуем доставку готовой картины до двери за 2 дня!\n\n✨ *Как начать:*\n- Сделайте фото комнаты (общий план при хорошем освещении)\n- Отправьте его в этот чат\n- Получите персональные варианты картин\n\n📬 После отправки фото с вами свяжется администратор для уточнения деталей. Начнем создавать ваш идеальный интерьер?");
            welcomeMessage.enableMarkdown(true);
            execute(welcomeMessage);
        } catch (TelegramApiException e) {
            logger.error("Error sending welcome message: {}", e.getMessage());
        }
        logger.info("User {} ({}) started the bot", message.getFrom().getId(), message.getFrom().getUserName());
    }
    
    private void handleHelp(Message message) {
        sendMessage(message.getChatId(), "🖼️ *Арт-мастерская - Справка*\n\n*Для клиентов:*\n📸 Отправьте фото комнаты для создания персональной картины\n💬 Администратор свяжется с вами для уточнения деталей\n\n*Для администраторов:*\n/reply <ID> <текст> - Ответить клиенту\n/users - Список активных клиентов\n\n🎨 Создаем идеальные картины для вашего интерьера!");
    }
    
    private void handleMessage(Message message) {
        User user = message.getFrom();
        Long userId = user.getId();
        
        logger.info("Processing message from user {} (admin: {})", userId, isAdmin(userId));
        
        // Если это администратор, обрабатываем как админское сообщение
        if (isAdmin(userId)) {
            logger.info("Admin message detected, handling as admin message");
            handleAdminMessage(message);
            return;
        }
        
        // Обычное сообщение от пользователя
        try {
            List<Long> adminIds = botConfig.getAdminIdsAsLong();
            
            // Пересылаем медиа файлы напрямую
            if (message.hasPhoto() || message.hasVideo() || message.hasDocument() || 
                message.hasVoice() || message.hasAudio() || message.hasSticker()) {
                
                logger.info("Forwarding media from user {} to {} admins", userId, adminIds.size());
                for (Long adminId : adminIds) {
                    forwardMessage(adminId, message);
                    
                    // Отправляем дополнительную информацию о пользователе
                    String userInfo = formatUserInfoForAdmin(message);
                    sendMessage(adminId, userInfo);
                }
                
            } else {
                // Текстовое сообщение
                String adminMessage = formatUserMessageForAdmin(message);
                logger.info("Forwarding text message to {} admins: {}", adminIds.size(), adminMessage.substring(0, Math.min(100, adminMessage.length())));
                for (Long adminId : adminIds) {
                    sendMessage(adminId, adminMessage);
                }
            }
            
            // Убираем подтверждение пользователю
            logger.info("Message from user {} forwarded to {} admins", userId, adminIds.size());
        } catch (Exception e) {
            logger.error("Error forwarding message to admins: ", e);
            sendMessage(message.getChatId(), "❌ Произошла ошибка. Попробуйте позже.");
        }
    }
    
    private void handleAdminMessage(Message message) {
        logger.info("Handling admin message: {}", message.hasText() ? message.getText() : "media message");
        
        // Проверяем, есть ли ожидающий медиа файл
        if (pendingMediaTargetUserId != null && (message.hasPhoto() || message.hasVideo() || 
            message.hasDocument() || message.hasVoice() || message.hasAudio() || message.hasSticker())) {
            
            logger.info("Sending admin media to user {}", pendingMediaTargetUserId);
            
            // Отправляем медиа файл
            sendMediaToUser(pendingMediaTargetUserId, message);
            
            sendMessage(message.getChatId(), "✅ Медиа файл отправлен пользователю " + pendingMediaTargetUserId);
            pendingMediaTargetUserId = null; // Сбрасываем ожидание
            return;
        }
        
        // Обработка сообщений от администратора (ответы на сообщения пользователей)
        if (message.isReply()) {
            logger.info("Admin reply message detected");
            Message repliedMessage = message.getReplyToMessage();
            if (repliedMessage != null) {
                // Извлекаем ID пользователя из пересланного сообщения
                String text = repliedMessage.getText();
                if (text != null && text.contains("ID: `")) {
                    String userIdStr = text.split("ID: `")[1].split("`")[0];
                    try {
                        Long targetUserId = Long.parseLong(userIdStr);
                        String replyText = message.getText();
                        sendMessage(targetUserId, replyText);
                        sendMessage(message.getChatId(), "✅ Ответ отправлен пользователю");
                        logger.info("Reply sent to user {}", targetUserId);
                    } catch (NumberFormatException e) {
                        sendMessage(message.getChatId(), "❌ Не удалось определить пользователя");
                        logger.error("Failed to parse user ID from reply");
                    }
                }
            }
        } else {
            logger.info("Admin sent regular message (not a reply)");
            String adminResponse = "👑 Вы администратор арт-мастерской. Для ответа клиентам используйте команду /reply или ответьте на пересланное сообщение.";
            sendMessage(message.getChatId(), adminResponse);
        }
    }
    
    private void handleAdminReply(Message message) {
        String[] parts = message.getText().split(" ", 2);
        if (parts.length < 2) {
            sendMessage(message.getChatId(), "❌ Использование: /reply <ID пользователя> [текст ответа]");
            return;
        }
        
        try {
            // Проверяем, есть ли текст после ID
            if (parts[1].contains(" ")) {
                // Есть текст ответа: /reply 153359354 Привет
                String[] textParts = parts[1].split(" ", 2);
                Long targetUserId = Long.parseLong(textParts[0]);
                String replyText = textParts[1];
                sendMessage(targetUserId, replyText);
                sendMessage(message.getChatId(), "✅ Текстовый ответ отправлен пользователю");
            } else {
                // Нет текста: /reply 153359354 - ждем медиа файл
                Long targetUserId = Long.parseLong(parts[1]);
                pendingMediaTargetUserId = targetUserId;
                sendMessage(message.getChatId(), "📤 Теперь отправьте медиа файл или фото для пользователя " + targetUserId);
            }
            
        } catch (NumberFormatException e) {
            sendMessage(message.getChatId(), "❌ Неверный ID пользователя");
        }
    }
    
    private void handleUsersList(Message message) {
        Map<Long, org.example.model.User> users = userService.getAllActiveUsers();
        List<Long> adminIds = botConfig.getAdminIdsAsLong();
        
        if (users.isEmpty()) {
            sendMessage(message.getChatId(), "📝 Активных пользователей нет");
            return;
        }
        
        StringBuilder sb = new StringBuilder("📝 Активные пользователи:\n\n");
        for (org.example.model.User user : users.values()) {
            sb.append(String.format("👤 %s\n", user.getFullName()));
            sb.append(String.format("🆔 ID: %d\n", user.getId()));
            if (user.getUsername() != null) {
                sb.append(String.format("👤 Username: @%s\n", user.getUsername()));
            }
            sb.append(String.format("🕐 Последняя активность: %s\n", user.getLastActivity()));
            
            // Показываем, является ли пользователь админом
            if (adminIds.contains(user.getId())) {
                sb.append("👑 Администратор\n");
            }
            sb.append("\n");
        }
        
        sendMessage(message.getChatId(), sb.toString());
    }
    
    private String formatUserMessageForAdmin(Message message) {
        User user = message.getFrom();
        StringBuilder sb = new StringBuilder();
        
        sb.append("📨 Новое сообщение от пользователя:\n\n");
        sb.append(String.format("👤 Пользователь: %s\n", user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : "")));
        sb.append(String.format("🆔 ID: %d\n", user.getId()));
        sb.append(String.format("👤 Username: @%s\n\n", user.getUserName() != null ? user.getUserName() : "Нет username"));
        
        if (message.hasText()) {
            sb.append("💬 Сообщение:\n");
            sb.append(message.getText());
        } else if (message.hasPhoto()) {
            sb.append("📷 Фото");
            if (message.getCaption() != null) {
                sb.append("\n💬 Подпись: ").append(message.getCaption());
            }
        } else if (message.hasVideo()) {
            sb.append("🎥 Видео");
            if (message.getCaption() != null) {
                sb.append("\n💬 Подпись: ").append(message.getCaption());
            }
        } else if (message.hasDocument()) {
            sb.append("📄 Документ: ").append(message.getDocument().getFileName());
        } else if (message.hasVoice()) {
            sb.append("🎤 Голосовое сообщение");
        } else if (message.hasAudio()) {
            sb.append("🎵 Аудио");
        } else if (message.hasSticker()) {
            sb.append("😀 Стикер");
        } else {
            sb.append("📎 Медиа файл");
        }
        
        return sb.toString();
    }
    
    private boolean isAdmin(Long userId) {
        List<Long> adminIds = botConfig.getAdminIdsAsLong();
        boolean isAdmin = adminIds.contains(userId);
        logger.debug("Checking if user {} is admin. Admin IDs: {}. Result: {}", userId, adminIds, isAdmin);
        return isAdmin;
    }
    
    private void sendMessage(Long chatId, String text) {
        try {
            logger.debug("Sending message to {}: '{}'", chatId, text);
            
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            sendMessage.enableMarkdown(false);
            sendMessage.enableHtml(false);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Error sending message to {}: {}", chatId, e.getMessage());
        }
    }
    
    private void forwardMessage(Long chatId, Message message) {
        try {
            logger.debug("Forwarding message {} to {}", message.getMessageId(), chatId);
            
            ForwardMessage forwardMessage = new ForwardMessage();
            forwardMessage.setChatId(chatId.toString());
            forwardMessage.setFromChatId(message.getChatId().toString());
            forwardMessage.setMessageId(message.getMessageId());
            execute(forwardMessage);
        } catch (TelegramApiException e) {
            logger.error("Error forwarding message to {}: {}", chatId, e.getMessage());
        }
    }
    
    private void sendMediaToUser(Long chatId, Message message) {
        try {
            logger.debug("Sending media to {}", chatId);
            
            if (message.hasPhoto()) {
                // Отправляем фото
                org.telegram.telegrambots.meta.api.methods.send.SendPhoto sendPhoto = 
                    new org.telegram.telegrambots.meta.api.methods.send.SendPhoto();
                sendPhoto.setChatId(chatId.toString());
                org.telegram.telegrambots.meta.api.objects.InputFile inputFile = 
                    new org.telegram.telegrambots.meta.api.objects.InputFile();
                inputFile.setMedia(message.getPhoto().get(0).getFileId());
                sendPhoto.setPhoto(inputFile);
                if (message.getCaption() != null) {
                    sendPhoto.setCaption(message.getCaption());
                }
                execute(sendPhoto);
                
            } else if (message.hasVideo()) {
                // Отправляем видео
                org.telegram.telegrambots.meta.api.methods.send.SendVideo sendVideo = 
                    new org.telegram.telegrambots.meta.api.methods.send.SendVideo();
                sendVideo.setChatId(chatId.toString());
                org.telegram.telegrambots.meta.api.objects.InputFile inputFile = 
                    new org.telegram.telegrambots.meta.api.objects.InputFile();
                inputFile.setMedia(message.getVideo().getFileId());
                sendVideo.setVideo(inputFile);
                if (message.getCaption() != null) {
                    sendVideo.setCaption(message.getCaption());
                }
                execute(sendVideo);
                
            } else if (message.hasDocument()) {
                // Отправляем документ
                org.telegram.telegrambots.meta.api.methods.send.SendDocument sendDocument = 
                    new org.telegram.telegrambots.meta.api.methods.send.SendDocument();
                sendDocument.setChatId(chatId.toString());
                org.telegram.telegrambots.meta.api.objects.InputFile inputFile = 
                    new org.telegram.telegrambots.meta.api.objects.InputFile();
                inputFile.setMedia(message.getDocument().getFileId());
                sendDocument.setDocument(inputFile);
                if (message.getCaption() != null) {
                    sendDocument.setCaption(message.getCaption());
                }
                execute(sendDocument);
                
            } else if (message.hasVoice()) {
                // Отправляем голосовое сообщение
                org.telegram.telegrambots.meta.api.methods.send.SendVoice sendVoice = 
                    new org.telegram.telegrambots.meta.api.methods.send.SendVoice();
                sendVoice.setChatId(chatId.toString());
                org.telegram.telegrambots.meta.api.objects.InputFile inputFile = 
                    new org.telegram.telegrambots.meta.api.objects.InputFile();
                inputFile.setMedia(message.getVoice().getFileId());
                sendVoice.setVoice(inputFile);
                if (message.getCaption() != null) {
                    sendVoice.setCaption(message.getCaption());
                }
                execute(sendVoice);
                
            } else if (message.hasAudio()) {
                // Отправляем аудио
                org.telegram.telegrambots.meta.api.methods.send.SendAudio sendAudio = 
                    new org.telegram.telegrambots.meta.api.methods.send.SendAudio();
                sendAudio.setChatId(chatId.toString());
                org.telegram.telegrambots.meta.api.objects.InputFile inputFile = 
                    new org.telegram.telegrambots.meta.api.objects.InputFile();
                inputFile.setMedia(message.getAudio().getFileId());
                sendAudio.setAudio(inputFile);
                if (message.getCaption() != null) {
                    sendAudio.setCaption(message.getCaption());
                }
                execute(sendAudio);
                
            } else if (message.hasSticker()) {
                // Отправляем стикер
                org.telegram.telegrambots.meta.api.methods.send.SendSticker sendSticker = 
                    new org.telegram.telegrambots.meta.api.methods.send.SendSticker();
                sendSticker.setChatId(chatId.toString());
                org.telegram.telegrambots.meta.api.objects.InputFile inputFile = 
                    new org.telegram.telegrambots.meta.api.objects.InputFile();
                inputFile.setMedia(message.getSticker().getFileId());
                sendSticker.setSticker(inputFile);
                execute(sendSticker);
            }
            
        } catch (TelegramApiException e) {
            logger.error("Error sending media to {}: {}", chatId, e.getMessage());
        }
    }
    
    private String formatUserInfoForAdmin(Message message) {
        User user = message.getFrom();
        StringBuilder sb = new StringBuilder();
        
        sb.append("📨 Медиа файл от пользователя:\n\n");
        sb.append(String.format("👤 Пользователь: %s\n", user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : "")));
        sb.append(String.format("🆔 ID: %d\n", user.getId()));
        sb.append(String.format("👤 Username: @%s\n", user.getUserName() != null ? user.getUserName() : "Нет username"));
        
        if (message.hasPhoto()) {
            sb.append("📷 Фото");
            if (message.getCaption() != null) {
                sb.append("\n💬 Подпись: ").append(message.getCaption());
            }
        } else if (message.hasVideo()) {
            sb.append("🎥 Видео");
            if (message.getCaption() != null) {
                sb.append("\n💬 Подпись: ").append(message.getCaption());
            }
        } else if (message.hasDocument()) {
            sb.append("📄 Документ: ").append(message.getDocument().getFileName());
        } else if (message.hasVoice()) {
            sb.append("🎤 Голосовое сообщение");
        } else if (message.hasAudio()) {
            sb.append("🎵 Аудио");
        } else if (message.hasSticker()) {
            sb.append("😀 Стикер");
        } else {
            sb.append("📎 Медиа файл");
        }
        
        return sb.toString();
    }
    
    private String formatMediaInfoForUser(Message message) {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        
        if (message.hasPhoto()) {
            sb.append("📷 Администратор отправил фото");
            if (message.getCaption() != null) {
                sb.append("\n💬 Подпись: ").append(message.getCaption());
            }
        } else if (message.hasVideo()) {
            sb.append("🎥 Администратор отправил видео");
            if (message.getCaption() != null) {
                sb.append("\n💬 Подпись: ").append(message.getCaption());
            }
        } else if (message.hasDocument()) {
            sb.append("📄 Администратор отправил документ: ").append(message.getDocument().getFileName());
            if (message.getCaption() != null) {
                sb.append("\n💬 Подпись: ").append(message.getCaption());
            }
        } else if (message.hasVoice()) {
            sb.append("🎤 Администратор отправил голосовое сообщение");
            if (message.getCaption() != null) {
                sb.append("\n💬 Подпись: ").append(message.getCaption());
            }
        } else if (message.hasAudio()) {
            sb.append("🎵 Администратор отправил аудио");
            if (message.getCaption() != null) {
                sb.append("\n💬 Подпись: ").append(message.getCaption());
            }
        } else if (message.hasSticker()) {
            sb.append("😀 Администратор отправил стикер");
        } else {
            sb.append("📎 Администратор отправил медиа файл");
        }
        
        return sb.toString();
    }
} 