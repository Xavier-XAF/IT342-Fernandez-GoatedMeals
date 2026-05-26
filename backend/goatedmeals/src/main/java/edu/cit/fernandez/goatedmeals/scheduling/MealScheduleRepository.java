package edu.cit.fernandez.goatedmeals.scheduling;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MealScheduleRepository extends JpaRepository<MealSchedule, Long> {
    // We will use this later to show the user their upcoming deliveries!
    List<MealSchedule> findByUserIdAndStatus(Long userId, String status);

    List<MealSchedule> findByStatus(String status);

    long countByStatus(String status);

    List<MealSchedule> findByUserId(Long userId);
}