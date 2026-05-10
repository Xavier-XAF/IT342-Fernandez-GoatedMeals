package edu.cit.fernandez.goatedmeals.repositories;

import edu.cit.fernandez.goatedmeals.models.MealSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MealScheduleRepository extends JpaRepository<MealSchedule, Long> {
    // We will use this later to show the user their upcoming deliveries!
    List<MealSchedule> findByUserIdAndStatus(Long userId, String status);
}