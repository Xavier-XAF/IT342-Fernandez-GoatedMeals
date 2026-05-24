package edu.cit.fernandez.goatedmeals.auth;

import edu.cit.fernandez.goatedmeals.core.security.JwtUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthFacade {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final List<AuthenticationStrategy> authStrategies;


    // The Facade handles the dependencies
    public AuthFacade(UserService userService, List<AuthenticationStrategy> authStrategies, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authStrategies = authStrategies;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, Object> authenticate(LoginRequest request) {
        // 1. Verify credentials via UserService
        String loginType = request.getLoginType() != null ? request.getLoginType() : "STANDARD";

        AuthenticationStrategy strategy = authStrategies.stream()
                .filter(s -> s.supports(loginType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported login type"));

        User user = strategy.authenticate(request);

        // 2. Use the Builder Pattern to construct the safe user object
        UserResponseDTO safeUser = buildSafeUser(user);

        // 3. Generate the token
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // 4. Structure the response payload
        Map<String, Object> dataPayload = new HashMap<>();
        dataPayload.put("user", safeUser);
        dataPayload.put("accessToken", accessToken);

        return formatSuccessResponse("Login successful", dataPayload);
    }

    public Map<String, Object> registerUser(RegisterRequest request) {
        // 1. Register the user
        User savedUser = userService.registerUser(request);

        // 2. Use the Builder Pattern to hide sensitive data
        UserResponseDTO safeUser = buildSafeUser(savedUser);

        return formatSuccessResponse("User registered successfully", safeUser);
    }

    // --- Helper Methods ---

    // This method demonstrates the Builder Pattern in action
    private UserResponseDTO buildSafeUser(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole())
                .build();
    }

    private Map<String, Object> formatSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
}