package edu.cit.fernandez.goatedmeals.billing;

import edu.cit.fernandez.goatedmeals.auth.User;
import edu.cit.fernandez.goatedmeals.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PayMongoService payMongoService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMySubscription(Authentication authentication) {
        // 1. Identify the user from their JWT token
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Look for their active subscription
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUserIdAndStatus(user.getId(), "ACTIVE");

        Map<String, Object> response = new HashMap<>();

        // 3. Package the data for React
        if (subscriptionOpt.isPresent()) {
            Subscription sub = subscriptionOpt.get();
            response.put("hasSubscription", true);
            response.put("planTier", sub.getPlanTier());
            response.put("availableCredits", sub.getAvailableCredits());
        } else {
            response.put("hasSubscription", false);
            response.put("availableCredits", 0);
        }

        return ResponseEntity.ok(response);
    }

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