package edu.cit.fernandez.goatedmeals.repositories;

import edu.cit.fernandez.goatedmeals.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserIdAndStatus(Long userId, String status);
}