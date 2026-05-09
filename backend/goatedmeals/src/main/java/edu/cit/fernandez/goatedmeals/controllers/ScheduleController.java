package edu.cit.fernandez.goatedmeals.controllers;

import edu.cit.fernandez.goatedmeals.models.Meal;
import edu.cit.fernandez.goatedmeals.models.MealSchedule;
import edu.cit.fernandez.goatedmeals.models.Subscription;
import edu.cit.fernandez.goatedmeals.models.User;
import edu.cit.fernandez.goatedmeals.repositories.MealRepository;
import edu.cit.fernandez.goatedmeals.repositories.MealScheduleRepository;
import edu.cit.fernandez.goatedmeals.repositories.SubscriptionRepository;
import edu.cit.fernandez.goatedmeals.repositories.UserRepository;
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
    public ResponseEntity<?> cancelSchedule(Authentication authentication, @PathVariable Long scheduleId) {
        try {
            // 1. Identify User
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Find the Schedule
            MealSchedule schedule = mealScheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));

            // 3. Security Check: Prevent users from canceling other people's meals
            if (!schedule.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Unauthorized action."));
            }

            // 4. Delete the schedule from the database
            mealScheduleRepository.delete(schedule);

            // 5. Refund 1 credit to their active subscription
            Subscription sub = subscriptionRepository.findByUserIdAndStatus(user.getId(), "ACTIVE")
                    .orElseThrow(() -> new RuntimeException("Active subscription not found"));

            sub.setAvailableCredits(sub.getAvailableCredits() + 1);
            subscriptionRepository.save(sub);

            return ResponseEntity.ok(Map.of(
                    "message", "Meal successfully cancelled! 1 credit refunded.",
                    "remainingCredits", sub.getAvailableCredits()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to cancel schedule"));
        }
    }

    @GetMapping("/my-schedule")
    public ResponseEntity<?> getMySchedule(Authentication authentication) {
        try {
            // 1. Identify User
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Fetch all their active schedules (ignoring old delivered ones for now)
            // We created this method in MealScheduleRepository earlier!
            List<MealSchedule> mySchedules = mealScheduleRepository.findByUserIdAndStatus(user.getId(), "SCHEDULED");

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