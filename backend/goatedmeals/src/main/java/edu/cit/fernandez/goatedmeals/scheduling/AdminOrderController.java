package edu.cit.fernandez.goatedmeals.scheduling; // Adjust package if needed

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {

    @Autowired
    private MealScheduleRepository mealScheduleRepository;

    @GetMapping("/active")
    public ResponseEntity<?> getAllActiveOrders() {
        try {
            // FIX: Fetch absolutely every order in the database instead of just "SCHEDULED"
            List<MealSchedule> allOrders = mealScheduleRepository.findAll();

            return ResponseEntity.ok(allOrders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch master order list"));
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> payload) {
        try {
            String newStatus = payload.get("status");

            // Find the order
            MealSchedule schedule = mealScheduleRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Update and save
            schedule.setStatus(newStatus);
            mealScheduleRepository.save(schedule);

            return ResponseEntity.ok(Map.of(
                    "message", "Order #" + orderId + " updated to " + newStatus,
                    "newStatus", newStatus
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
}