package edu.cit.fernandez.goatedmeals.billing;

import edu.cit.fernandez.goatedmeals.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayMongoService {

    @Value("${paymongo.secret.key}")
    private String secretKey;

    @Value("${paymongo.api.url}")
    private String apiUrl;

    @Autowired
    private PaymentRepository paymentRepository;

    public String createCheckoutSession(User user, String planTier, BigDecimal amount) {
        RestTemplate restTemplate = new RestTemplate();

        // PayMongo uses Basic Auth with the secret key as the username
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // Amount must be in cents (e.g., PHP 1490.00 -> 149000)
        int amountInCents = amount.multiply(new BigDecimal("100")).intValue();

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("currency", "PHP");
        lineItem.put("amount", amountInCents);
        lineItem.put("name", planTier + " Subscription");
        lineItem.put("quantity", 1);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("line_items", List.of(lineItem));
        attributes.put("payment_method_types", List.of("card", "gcash", "paymaya"));
        attributes.put("success_url", "http://localhost:3000/billing?status=success");
        attributes.put("cancel_url", "http://localhost:3000/billing?status=cancelled");
        attributes.put("description", "Subscription for " + user.getEmail());

        Map<String, Object> data = new HashMap<>();
        data.put("attributes", attributes);

        Map<String, Object> body = new HashMap<>();
        body.put("data", data);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl + "/checkout_sessions", request, Map.class);
            Map<String, Object> responseData = (Map<String, Object>) response.getBody().get("data");
            Map<String, Object> responseAttributes = (Map<String, Object>) responseData.get("attributes");

            String checkoutUrl = (String) responseAttributes.get("checkout_url");
            String checkoutSessionId = (String) responseData.get("id"); // Retrieve the ID for webhook tracking

            // Record the pending payment in the database
            Payment payment = new Payment();
            payment.setUser(user);
            payment.setTransactionId(checkoutSessionId);
            payment.setAmount(amount);
            payment.setStatus("PENDING");
            paymentRepository.save(payment);

            return checkoutUrl;
        } catch (Exception e) {
            System.err.println("PayMongo API Error: " + e.getMessage());
            throw new RuntimeException("Failed to create PayMongo checkout session");
        }
    }
}