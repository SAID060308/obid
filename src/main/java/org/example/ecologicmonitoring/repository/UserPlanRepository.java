package org.example.ecologicmonitoring.repository;

import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.entity.UserPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserPlanRepository extends JpaRepository<UserPlan, Long> {
    List<UserPlan> findByUserOrderByPlanDateAsc(User user);
    Optional<UserPlan> findByUserAndPlanDate(User user, LocalDate planDate);
}