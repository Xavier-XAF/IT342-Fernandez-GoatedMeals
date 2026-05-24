package edu.cit.fernandez.goatedmeals.menu;

import edu.cit.fernandez.goatedmeals.core.storage.SupabaseStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/meals")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminMealController {

    private final MealService mealService;
    private final SupabaseStorageService storageService;

    public AdminMealController(MealService mealService, SupabaseStorageService storageService) {
        this.mealService = mealService;
        this.storageService = storageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createMeal(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("imageFile") MultipartFile imageFile) {

        try {
            Meal mealToSave = new Meal();
            mealToSave.setName(name);
            mealToSave.setCategory(category);
            mealToSave.setDescription(description);

            // 1. Upload the real image to Supabase
            String publicImageUrl = storageService.uploadImage(imageFile);
            mealToSave.setImageUrl(publicImageUrl);

            // 2. Save the meal record in the database
            Meal savedMeal = mealService.createMeal(mealToSave);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedMeal);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Gets all meals for the catalog grid
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMeals() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", mealService.getAllMeals());

        return ResponseEntity.ok(response);
    }

    // 1. DELETE A MEAL
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMeal(@PathVariable Long id) {
        mealService.deleteMeal(id); // You'll need to add this method to your MealService!

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Meal deleted successfully");
        return ResponseEntity.ok(response);
    }

    // 2. EDIT A MEAL
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateMeal(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        try {
            // Find existing meal
            Meal meal = mealService.getMealById(id);
            meal.setName(name);
            meal.setCategory(category);
            meal.setDescription(description);

            // Only upload a new image if the user actually picked one
            if (imageFile != null && !imageFile.isEmpty()) {
                String newImageUrl = storageService.uploadImage(imageFile);
                meal.setImageUrl(newImageUrl);
            }

            Meal updatedMeal = mealService.updateMeal(meal);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedMeal);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}