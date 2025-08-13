package com.example.clinic.controller;

import com.example.clinic.model.Doctor;
import com.example.clinic.model.User;
import com.example.clinic.repo.UserRepository;
import com.example.clinic.service.SessionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import com.example.clinic.repo.DoctorRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final SessionService sessions;
    private final DoctorRepository docRepo;

    public AuthController(UserRepository userRepo, SessionService sessions, DoctorRepository docRepo) {
        this.userRepo = userRepo;
        this.sessions = sessions;
        this.docRepo=docRepo;
    }

    public static class RegisterRequest {

        public String name;
        public String username;
        public String password;
        public String role; // "USER" or "DOCTOR"
    }

    public static class LoginRequest {

        public String username;
        public String password;
        public String role; // "USER" or "DOCTOR"
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> userOpt = userRepo.findByUsername(req.username);
        User user = userOpt.filter(u -> u.getPassword().equals(req.password)).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        if (req.role == null || !user.getRole().name().equals(req.role)) {
            return ResponseEntity.status(401).body("Role mismatch");
        }
        String token = sessions.createSession(user.getUsername());
        return ResponseEntity.ok(Map.of("token", token, "role", user.getRole().name(), "username", user.getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.name == null || req.name.isBlank() || req.username == null || req.username.isBlank() || req.password == null || req.password.isBlank() || req.role == null) {
            return ResponseEntity.badRequest().body("Name, Username, password and role required");
        }
        if (userRepo.findByUsername(req.username).isPresent()) {
            return ResponseEntity.badRequest().body("Username taken");
        }
        User.Role role;
        try {
            role = User.Role.valueOf(req.role);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid role");
        }
        User saved = userRepo.save(new User(req.name, req.username, req.password, role));
        if(saved.getRole().equals(User.Role.DOCTOR)){
            Doctor addDoctor=docRepo.save(new Doctor(req.name, "none",saved));
        }

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        if (token != null) {
            sessions.invalidate(token);
        }
        return ResponseEntity.noContent().build();
    }
}
