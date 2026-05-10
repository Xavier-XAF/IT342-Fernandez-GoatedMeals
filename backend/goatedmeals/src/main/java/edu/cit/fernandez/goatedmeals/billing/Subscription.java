package edu.cit.fernandez.goatedmeals.billing;

import edu.cit.fernandez.goatedmeals.auth.User;
import jakarta.persistence.*;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "plan_tier", nullable = false, length = 50)
    private String planTier;

    @Column(name = "total_credits_allowed", nullable = false)
    private Integer totalCreditsAllowed;

    @Column(name = "available_credits", nullable = false)
    private Integer availableCredits;

    @Column(nullable = false, length = 20)
    private String status; // e.g., ACTIVE, INACTIVE, CANCELLED

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPlanTier() { return planTier; }
    public void setPlanTier(String planTier) { this.planTier = planTier; }

    public Integer getTotalCreditsAllowed() { return totalCreditsAllowed; }
    public void setTotalCreditsAllowed(Integer totalCreditsAllowed) { this.totalCreditsAllowed = totalCreditsAllowed; }

    public Integer getAvailableCredits() { return availableCredits; }
    public void setAvailableCredits(Integer availableCredits) { this.availableCredits = availableCredits; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}