package edu.cit.fernandez.goatedmeals.controllers;

import edu.cit.fernandez.goatedmeals.dtos.LoginRequest;
import edu.cit.fernandez.goatedmeals.dtos.RegisterRequest;
import edu.cit.fernandez.goatedmeals.models.User;
import edu.cit.fernandez.goatedmeals.security.JwtUtil;
import edu.cit.fernandez.goatedmeals.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // --- SECURED LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.loginUser(request);

            // Hide password
            Map<String, Object> safeUser = new HashMap<>();
            safeUser.put("id", user.getId());
            safeUser.put("email", user.getEmail());
            safeUser.put("firstname", user.getFirstname());
            safeUser.put("lastname", user.getLastname());
            safeUser.put("role", user.getRole());

            // Generate token
            String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

            Map<String, Object> dataPayload = new HashMap<>();
            dataPayload.put("user", safeUser);
            dataPayload.put("accessToken", accessToken);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("data", dataPayload);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    // --- SECURED REGISTER ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User savedUser = userService.registerUser(request);

            // Hide password
            Map<String, Object> safeUser = new HashMap<>();
            safeUser.put("id", savedUser.getId());
            safeUser.put("email", savedUser.getEmail());
            safeUser.put("firstname", savedUser.getFirstname());
            safeUser.put("lastname", savedUser.getLastname());
            safeUser.put("role", savedUser.getRole());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("data", safeUser); // Return the safe object instead of the raw user

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}