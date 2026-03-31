package org.example.ecologicmonitoring.repository;

import org.example.ecologicmonitoring.entity.User;
import org.example.ecologicmonitoring.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherData, Long> {

    List<WeatherData> findByUserAndTimestampBeforeOrderByTimestampDesc(
            User user, LocalDateTime timestamp
    );

    List<WeatherData> findByCityOrderByTimestampAsc(String city);

    // Bu methodni to'g'irlang:
    Optional<WeatherData> findByCityAndTimestamp(String city, LocalDateTime timestamp);

    List<WeatherData> findByUserAndCityOrderByTimestampAsc(User user, String city);

    // Fresh ma'lumotlar (timestamp dan keyin)
    List<WeatherData> findByUserAndCityAndTimestampAfterOrderByTimestampAsc(
            User user, String city, LocalDateTime timestamp
    );

    // Duplicate tekshirish
    Optional<WeatherData> findByUserAndCityAndTimestamp(
            User user, String city, LocalDateTime timestamp
    );

    // Tarix uchun — o'tgan ma'lumotlar
    List<WeatherData> findByUserAndCityAndTimestampBeforeOrderByTimestampDesc(
            User user, String city, LocalDateTime timestamp
    );
}