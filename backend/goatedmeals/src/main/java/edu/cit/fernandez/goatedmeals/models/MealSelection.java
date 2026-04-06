package edu.cit.fernandez.goatedmeals.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "meal_selections")
public class MealSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    private LocalDate selectionDate;

}