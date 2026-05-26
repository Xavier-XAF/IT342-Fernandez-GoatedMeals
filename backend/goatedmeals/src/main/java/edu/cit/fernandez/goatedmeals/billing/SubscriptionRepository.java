package edu.cit.fernandez.goatedmeals.billing;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserIdAndStatus(Long userId, String status);

    Optional<Subscription> findByUserId(Long userId);

    long countByStatus(String status);
}