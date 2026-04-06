package edu.cit.fernandez.goatedmeals.repositories;

import edu.cit.fernandez.goatedmeals.models.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

}