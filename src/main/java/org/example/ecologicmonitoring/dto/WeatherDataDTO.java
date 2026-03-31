package org.example.ecologicmonitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WeatherDataDTO {
    private String city;
    private Double temperature;
    private Double humidity;
    private Double pressure;
    private Double windSpeed;
    private LocalDateTime timestamp;
}