package edu.cit.fernandez.goatedmeals.menu;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealService {

    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    // Save a new meal to Supabase
    public Meal createMeal(Meal meal) {
        return mealRepository.save(meal);
    }

    // Fetch all meals for the catalog
    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public Meal getMealById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found with id: " + id));
    }

    // UPDATE
    public Meal updateMeal(Meal meal) {
        // Ensure the meal exists before trying to save it
        if (meal.getId() == null || !mealRepository.existsById(meal.getId())) {
            throw new RuntimeException("Cannot update meal. ID not found.");
        }
        return mealRepository.save(meal);
    }

    // DELETE
    public void deleteMeal(Long id) {
        if (!mealRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete meal. ID " + id + " does not exist.");
        }
        mealRepository.deleteById(id);
    }
}