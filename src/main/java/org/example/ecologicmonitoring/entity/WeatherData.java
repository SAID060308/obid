package org.example.ecologicmonitoring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private Double temperature;
    private Double humidity;
    private Double pressure;
    private Double windSpeed;
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Override
    public String toString() {
        return "WeatherData{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", windSpeed=" + windSpeed +
                ", timestamp=" + timestamp +
                '}';
    }
}
