package edu.cit.fernandez.goatedmeals.controllers;

import edu.cit.fernandez.goatedmeals.models.User;
import edu.cit.fernandez.goatedmeals.repositories.UserRepository;
import edu.cit.fernandez.goatedmeals.services.PayMongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    @Autowired
    private PayMongoService payMongoService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/pay")
    public ResponseEntity<Map<String, Object>> initiatePayment(
            Authentication authentication,
            @RequestBody Map<String, Object> payload) {

        // Ensure user is authenticated via JWT
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        String planTier = (String) payload.get("planTier");
        // Using BigDecimal to avoid floating-point inaccuracies with currency
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());

        String checkoutUrl = payMongoService.createCheckoutSession(user, planTier, amount);

        Map<String, Object> data = new HashMap<>();
        data.put("checkoutUrl", checkoutUrl);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}