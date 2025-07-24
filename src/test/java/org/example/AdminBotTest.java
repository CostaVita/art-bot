package org.example;

import org.example.config.BotConfig;
import org.example.model.User;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "bot.admin.id=123456789",
    "bot.messages.welcome=Test welcome message",
    "bot.messages.message-sent=Test message sent",
    "bot.messages.admin-reply=Test admin reply",
    "bot.messages.error=Test error",
    "bot.messages.unauthorized=Test unauthorized",
    "bot.messages.help=Test help"
})
public class AdminBotTest {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private UserService userService;

    @Test
    void testBotConfig() {
        assertNotNull(botConfig);
        assertEquals("123456789", botConfig.getAdminId());
        assertEquals(123456789L, botConfig.getAdminIdAsLong());
        assertNotNull(botConfig.getMessages());
        assertEquals("Test welcome message", botConfig.getMessages().getWelcome());
        assertEquals("Test message sent", botConfig.getMessages().getMessageSent());
    }

    @Test
    void testUserService() {
        assertNotNull(userService);
        
        // Тестируем добавление пользователя
        Long userId = 12345L;
        userService.addOrUpdateUser(userId, "testuser", "Test", "User");
        
        User user = userService.getUser(userId);
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("Test User", user.getFullName());
        
        // Тестируем обновление пользователя
        userService.addOrUpdateUser(userId, "updateduser", "Updated", "User");
        User updatedUser = userService.getUser(userId);
        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("Updated", updatedUser.getFirstName());
        
        // Тестируем список пользователей
        assertEquals(1, userService.getActiveUsersCount());
        assertTrue(userService.isUserActive(userId));
        
        // Тестируем удаление пользователя
        userService.removeUser(userId);
        assertFalse(userService.isUserActive(userId));
        assertEquals(0, userService.getActiveUsersCount());
    }

    @Test
    void testUserModel() {
        User user = new User(123L, "testuser", "Test", "User");
        
        assertEquals(123L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("Test User", user.getFullName());
        
        // Тестируем пользователя без фамилии
        User userWithoutLastName = new User(456L, "testuser2", "Test", null);
        assertEquals("Test", userWithoutLastName.getFullName());
        
        // Тестируем обновление активности
        assertNotNull(user.getLastActivity());
        user.updateActivity();
        assertNotNull(user.getLastActivity());
    }
} 