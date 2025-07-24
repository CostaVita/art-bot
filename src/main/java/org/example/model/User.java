package org.example.model;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDateTime lastActivity;
    
    public User() {}
    
    public User(Long id, String username, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastActivity = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
    
    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
    }
    
    public String getFullName() {
        if (lastName != null && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        }
        return firstName;
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', name='%s', lastActivity=%s}", 
                           id, username, getFullName(), lastActivity);
    }
} 