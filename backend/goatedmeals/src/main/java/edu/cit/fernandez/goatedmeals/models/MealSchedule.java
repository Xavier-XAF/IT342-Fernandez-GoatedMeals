package edu.cit.fernandez.goatedmeals.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "meal_schedules")
public class MealSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who ordered the meal
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The specific meal they selected
    @ManyToOne
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    // e.g., "Monday", "Tuesday", or a specific date
    @Column(name = "delivery_day")
    private String deliveryDay;

    // e.g., "SCHEDULED", "PREPARING", "DELIVERED"
    @Column(nullable = false)
    private String status = "SCHEDULED";

    @Column(name = "delivery_method")
    private String deliveryMethod; // "Delivery" or "Pickup"

    @Column(name = "delivery_address")
    private String deliveryAddress;

    // Standard Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Meal getMeal() { return meal; }
    public void setMeal(Meal meal) { this.meal = meal; }
    public String getDeliveryDay() { return deliveryDay; }
    public void setDeliveryDay(String deliveryDay) { this.deliveryDay = deliveryDay; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}