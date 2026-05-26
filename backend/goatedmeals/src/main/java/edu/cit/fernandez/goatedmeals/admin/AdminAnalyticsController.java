package edu.cit.fernandez.goatedmeals.admin; // Adjust package if needed

import edu.cit.fernandez.goatedmeals.auth.UserRepository;
import edu.cit.fernandez.goatedmeals.billing.SubscriptionRepository;
import edu.cit.fernandez.goatedmeals.scheduling.MealScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/analytics")
public class AdminAnalyticsController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private MealScheduleRepository mealScheduleRepository;

    @GetMapping
    public ResponseEntity<?> getDashboardStats() {
        try {
            long totalUsers = userRepository.count();
            long activeSubscribers = subscriptionRepository.countByStatus("ACTIVE");
            long totalOrders = mealScheduleRepository.count();
            long pendingDeliveries = mealScheduleRepository.countByStatus("SCHEDULED")
                    + mealScheduleRepository.countByStatus("PREPARING")
                    + mealScheduleRepository.countByStatus("DELIVERING");

            // Calculate rough revenue (assuming each sub is 2500 PHP)
            long estimatedRevenue = activeSubscribers * 2500;

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("activeSubscribers", activeSubscribers);
            stats.put("totalOrders", totalOrders);
            stats.put("pendingDeliveries", pendingDeliveries);
            stats.put("estimatedRevenue", estimatedRevenue);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load analytics"));
        }
    }
}