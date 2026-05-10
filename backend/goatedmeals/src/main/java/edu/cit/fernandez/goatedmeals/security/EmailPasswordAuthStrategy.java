package edu.cit.fernandez.goatedmeals.security;

import edu.cit.fernandez.goatedmeals.dtos.LoginRequest;
import edu.cit.fernandez.goatedmeals.models.User;
import edu.cit.fernandez.goatedmeals.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class EmailPasswordAuthStrategy implements AuthenticationStrategy {

    private final UserRepository userRepository;
    // Inject BCryptPasswordEncoder here later!

    public EmailPasswordAuthStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(String loginType) {
        return "STANDARD".equalsIgnoreCase(loginType);
    }

    @Override
    public User authenticate(LoginRequest request) {
        // Your existing login logic goes here
        // 1. Find user by email
        // 2. Check BCrypt password hash
        // 3. Return user or throw RuntimeException("Invalid credentials")

        return userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
