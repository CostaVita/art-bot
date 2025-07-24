package org.example.service;

import org.example.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    
    private final Map<Long, User> activeUsers = new ConcurrentHashMap<>();
    
    public void addOrUpdateUser(Long userId, String username, String firstName, String lastName) {
        User user = activeUsers.get(userId);
        if (user != null) {
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.updateActivity();
        } else {
            user = new User(userId, username, firstName, lastName);
            activeUsers.put(userId, user);
        }
    }
    
    public User getUser(Long userId) {
        return activeUsers.get(userId);
    }
    
    public Map<Long, User> getAllActiveUsers() {
        return new ConcurrentHashMap<>(activeUsers);
    }
    
    public boolean isUserActive(Long userId) {
        return activeUsers.containsKey(userId);
    }
    
    public void removeUser(Long userId) {
        activeUsers.remove(userId);
    }
    
    public int getActiveUsersCount() {
        return activeUsers.size();
    }
} 