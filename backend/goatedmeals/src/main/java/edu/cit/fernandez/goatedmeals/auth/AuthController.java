package edu.cit.fernandez.goatedmeals.auth;

import edu.cit.fernandez.goatedmeals.auth.LoginRequest;
import edu.cit.fernandez.goatedmeals.auth.RegisterRequest;
import edu.cit.fernandez.goatedmeals.auth.UserResponseDTO;
import edu.cit.fernandez.goatedmeals.auth.User;
import edu.cit.fernandez.goatedmeals.auth.UserRepository;
import edu.cit.fernandez.goatedmeals.auth.AuthFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthFacade authFacade;
    private final UserRepository userRepository;

    public AuthController(AuthFacade authFacade, UserRepository userRepository) {
        this.authFacade = authFacade;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // The controller delegates everything to the Facade
            Map<String, Object> response = authFacade.authenticate(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // The controller delegates everything to the Facade
            Map<String, Object> response = authFacade.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            // The JwtAuthenticationFilter sets the authentication context using the email
            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Utilize your existing DTO to send a clean, secure response without exposing passwords
            UserResponseDTO userResponse = UserResponseDTO.builder()
                    .id(user.getId())
                    .firstname(user.getFirstname()) // Use .firstname() if this gives a red line
                    .lastname(user.getLastname())   // Use .lastname() if this gives a red line
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();

            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Session invalid or expired. Please log in again."));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody Map<String, String> updates) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Notice how it grabs "phone" from React but saves it to "contactNumber"
            if (updates.containsKey("phone")) {
                user.setContactNumber(updates.get("phone"));
            }
            if (updates.containsKey("defaultAddress")) {
                user.setDefaultAddress(updates.get("defaultAddress"));
            }

            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Helper for error formatting
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}