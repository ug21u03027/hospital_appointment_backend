package com.example.clinic.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    // token -> username
    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    public String createSession(String username) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, username);
        return token;
    }

    public boolean isValid(String token) {
        return token != null && sessions.containsKey(token);
    }

    public String getUsername(String token) {
        return sessions.get(token);
    }

    public void invalidate(String token) {
        sessions.remove(token);
    }
}
