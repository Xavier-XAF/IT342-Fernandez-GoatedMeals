package edu.cit.fernandez.goatedmeals.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class GoogleOAuthStrategy implements AuthenticationStrategy {

    private final UserRepository userRepository;
    private final GoogleIdTokenVerifier verifier;

    // Inject the Client ID from application.properties
    public GoogleOAuthStrategy(UserRepository userRepository,
                               @Value("${google.client.id}") String clientId) {
        this.userRepository = userRepository;

        // Initialize the official Google Token Verifier
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    @Override
    public boolean supports(String loginType) {
        return "GOOGLE".equalsIgnoreCase(loginType);
    }

    @Override
    public User authenticate(LoginRequest request) {
        String idTokenString = request.getGoogleIdToken();

        if (idTokenString == null || idTokenString.isEmpty()) {
            throw new RuntimeException("Google ID token is missing");
        }

        try {
            // 1. Verify the token securely with Google
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // 2. Extract user details from the Google Payload
                String email = payload.getEmail();
                String firstName = (String) payload.get("given_name");
                String lastName = (String) payload.get("family_name");
                String googleProviderId = payload.getSubject(); // Google's unique user ID

                // 3. Find existing user, or create a new one if this is their first login
                Optional<User> existingUser = userRepository.findByEmail(email);

                if (existingUser.isPresent()) {
                    return existingUser.get();
                } else {
                    // Auto-register the new user via Google
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstname(firstName != null ? firstName : "Google");
                    newUser.setLastname(lastName != null ? lastName : "User");
                    newUser.setOauthProviderId(googleProviderId);
                    newUser.setRole("USER"); // Default role

                    // We generate a random dummy password because BCrypt requires one,
                    // even though they will always use Google to log in.
                    newUser.setPasswordHash("OAUTH_NO_PASSWORD");

                    return userRepository.save(newUser);
                }
            } else {
                throw new RuntimeException("Invalid Google ID token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Google token: " + e.getMessage());
        }
    }
}

