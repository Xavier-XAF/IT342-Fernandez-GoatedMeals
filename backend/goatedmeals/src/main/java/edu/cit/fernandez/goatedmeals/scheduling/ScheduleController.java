package edu.cit.fernandez.goatedmeals.scheduling;

import edu.cit.fernandez.goatedmeals.menu.Meal;
import edu.cit.fernandez.goatedmeals.billing.Subscription;
import edu.cit.fernandez.goatedmeals.auth.User;
import edu.cit.fernandez.goatedmeals.menu.MealRepository;
import edu.cit.fernandez.goatedmeals.billing.SubscriptionRepository;
import edu.cit.fernandez.goatedmeals.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MealRepository mealRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private MealScheduleRepository mealScheduleRepository;

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> cancelSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            MealSchedule schedule = mealScheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));

            // 1. Security Check: Does this order actually belong to this user?
            if (!schedule.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Unauthorized to cancel this order"));
            }

            // --- 2. NEW STRICT CHECK: Only allow cancellation if SCHEDULED ---
            if (!schedule.getStatus().equals("SCHEDULED")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Too late! You can only cancel orders that are still SCHEDULED."
                ));
            }

            // 3. Refund the credit
            Subscription sub = subscriptionRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Subscription not found"));

            sub.setAvailableCredits(sub.getAvailableCredits() + 1);
            subscriptionRepository.save(sub);

            // 4. Delete the schedule
            mealScheduleRepository.delete(schedule);

            return ResponseEntity.ok(Map.of("message", "Delivery canceled. 1 Credit has been refunded."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-schedule")
    public ResponseEntity<?> getMySchedule(Authentication authentication) {
        try {
            // 1. Identify User
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // FIX: Fetch ALL of their schedules (Scheduled, Preparing, Delivered)
            List<MealSchedule> mySchedules = mealScheduleRepository.findByUserId(user.getId());

            return ResponseEntity.ok(mySchedules);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch schedule"));
        }
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookMeal(Authentication authentication, @RequestBody Map<String, Object> payload) {
        try {
            // 1. Identify User
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Check their active subscription and credits
            Subscription sub = subscriptionRepository.findByUserIdAndStatus(user.getId(), "ACTIVE")
                    .orElseThrow(() -> new RuntimeException("No active subscription found. Please subscribe first."));

            if (sub.getAvailableCredits() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "You have no available meal credits left."));
            }

            // 3. Find the requested meal
            Long mealId = Long.valueOf(payload.get("mealId").toString());
            String deliveryDay = (String) payload.get("deliveryDay");
            Meal meal = mealRepository.findById(mealId)
                    .orElseThrow(() -> new RuntimeException("Meal not found"));

            // 4. Save the schedule
            MealSchedule schedule = new MealSchedule();
            schedule.setUser(user);
            schedule.setMeal(meal);
            schedule.setDeliveryDay((String) payload.get("deliveryDate"));
            schedule.setDeliveryTime((String) payload.get("deliveryTime"));
            schedule.setDeliveryMethod((String) payload.get("deliveryMethod"));
            schedule.setDeliveryAddress((String) payload.get("deliveryAddress"));

            mealScheduleRepository.save(schedule);

            // 5. Deduct 1 credit and save the subscription
            sub.setAvailableCredits(sub.getAvailableCredits() - 1);
            subscriptionRepository.save(sub);

            return ResponseEntity.ok(Map.of(
                    "message", "Meal successfully scheduled!",
                    "remainingCredits", sub.getAvailableCredits()
            ));

        } catch (Exception e) {
            e.printStackTrace(); // This will print exact errors to your terminal if something breaks
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}