package com.krish.AgariBackend.dto;

// import java.util.List;
import java.util.Set;

public class RegisterResponceDto {

    private String message;
    private String username;
    private Set<String> role;

    public RegisterResponceDto(String message, String username, Set<String> set) {
        this.message = message;
        this.username = username;
        this.role = (Set<String>) set;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return role;
    }
}
