package edu.cit.fernandez.goatedmeals.controllers;

import edu.cit.fernandez.goatedmeals.models.Payment;
import edu.cit.fernandez.goatedmeals.models.Subscription;
import edu.cit.fernandez.goatedmeals.models.User;
import edu.cit.fernandez.goatedmeals.repositories.PaymentRepository;
import edu.cit.fernandez.goatedmeals.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @PostMapping("/paymongo")
    public ResponseEntity<String> handlePayMongoWebhook(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
            String eventType = (String) attributes.get("type");

            if ("checkout_session.payment.paid".equals(eventType)) {
                Map<String, Object> eventData = (Map<String, Object>) attributes.get("data");
                String checkoutSessionId = (String) eventData.get("id");

                // Find the pending payment we recorded earlier
                Optional<Payment> optionalPayment = paymentRepository.findByTransactionId(checkoutSessionId);

                if (optionalPayment.isPresent()) {
                    Payment payment = optionalPayment.get();
                    payment.setStatus("SUCCESS");
                    paymentRepository.save(payment);

                    // Update or create the user's subscription
                    User user = payment.getUser();

                    // Standardizes giving 7 credits per successful premium weekly transaction
                    Subscription subscription = subscriptionRepository.findByUserIdAndStatus(user.getId(), "ACTIVE")
                            .orElse(new Subscription());

                    subscription.setUser(user);
                    subscription.setPlanTier("PREMIUM_WEEKLY"); // Typically extracted dynamically in a full implementation
                    subscription.setStatus("ACTIVE");
                    subscription.setTotalCreditsAllowed(7);

                    // Add to existing credits or start fresh
                    int currentCredits = subscription.getAvailableCredits() != null ? subscription.getAvailableCredits() : 0;
                    subscription.setAvailableCredits(currentCredits + 7);

                    subscriptionRepository.save(subscription);
                }
            }
            return ResponseEntity.ok("Webhook processed");

        } catch (Exception e) {
            System.err.println("Error processing webhook: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to process webhook");
        }
    }
}