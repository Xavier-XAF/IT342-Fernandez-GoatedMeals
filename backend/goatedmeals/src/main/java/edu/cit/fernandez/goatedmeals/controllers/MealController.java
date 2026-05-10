package edu.cit.fernandez.goatedmeals.controllers;

import edu.cit.fernandez.goatedmeals.models.Meal;
import edu.cit.fernandez.goatedmeals.repositories.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meals")
public class MealController {

    @Autowired
    private MealRepository mealRepository;

    // A simple GET endpoint that anyone with a valid token can access
    @GetMapping
    public ResponseEntity<List<Meal>> getAllMeals() {
        List<Meal> meals = mealRepository.findAll();
        return ResponseEntity.ok(meals);
    }
}