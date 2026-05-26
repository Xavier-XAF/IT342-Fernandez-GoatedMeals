package edu.cit.fernandez.goatedmeals.auth;

import edu.cit.fernandez.goatedmeals.auth.LoginRequest;
import edu.cit.fernandez.goatedmeals.auth.RegisterRequest;
import edu.cit.fernandez.goatedmeals.auth.UserResponseDTO;
import edu.cit.fernandez.goatedmeals.auth.User;
import edu.cit.fernandez.goatedmeals.auth.UserRepository;
import edu.cit.fernandez.goatedmeals.auth.AuthFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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

            // --- The New Fields (Name & Email) ---
            if (updates.containsKey("firstname")) user.setFirstname(updates.get("firstname"));
            if (updates.containsKey("lastname")) user.setLastname(updates.get("lastname"));
            if (updates.containsKey("email")) user.setEmail(updates.get("email"));

            // --- Your Existing Fields (Phone & Address) ---
            if (updates.containsKey("phone")) {
                user.setContactNumber(updates.get("phone"));
            }
            if (updates.containsKey("defaultAddress")) {
                user.setDefaultAddress(updates.get("defaultAddress"));
            }

            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Profile updated successfully!",
                    "user", user // Sending the updated user back to React
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody Map<String, String> payload) {
        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String currentPassword = payload.get("currentPassword");
            String newPassword = payload.get("newPassword");

            // 1. Verify the current password is correct using getPasswordHash()
            if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Current password is incorrect."));
            }

            // 2. Hash and save the new password using setPasswordHash()
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Password updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update password."));
        }
    }

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(Authentication authentication) {
        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Depending on your database cascading rules, this will wipe the user
            userRepository.delete(user);

            return ResponseEntity.ok(Map.of("message", "Account completely deleted."));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "Cannot delete account. Ensure all active orders are canceled first."));
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