package org.example.ecologicmonitoring.repository;

import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.entity.UserCity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCityRepository extends JpaRepository<UserCity, Long> {
    List<UserCity> findByUserOrderByAddedAtDesc(User user);
    boolean existsByUserAndCityName(User user, String cityName);
    void deleteByIdAndUser(Long id, User user);
}