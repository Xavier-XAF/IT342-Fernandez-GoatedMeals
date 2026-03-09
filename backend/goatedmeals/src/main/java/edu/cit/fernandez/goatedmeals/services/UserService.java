package edu.cit.fernandez.goatedmeals.services;

import edu.cit.fernandez.goatedmeals.dtos.LoginRequest;
import edu.cit.fernandez.goatedmeals.dtos.RegisterRequest;
import edu.cit.fernandez.goatedmeals.models.User;
import edu.cit.fernandez.goatedmeals.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest request) {
        // 1. Checks if the email is already taken
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email is already registered!");
            // We will upgrade this to a proper global exception later!
        }

        // 2. Create a new User entity and map the data
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setFirstname(request.getFirstname());
        newUser.setLastname(request.getLastname());
        newUser.setContactNumber(request.getContactNumber());

        // Note: We are temporarily saving the raw password.
        // We MUST add BCrypt hashing before connecting the frontend!
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("USER"); // Default role

        // 3. Save to Supabase
        return userRepository.save(newUser);
    }

    public User loginUser(LoginRequest request) {
        // 1. Find the user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // 2. Compare the raw password with the encoded hash in DB
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 3. If match, return the user (In Week 3, we will return a JWT Token here!)
        return user;
    }
}