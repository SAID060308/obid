package org.example.ecologicmonitoring.external.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.security.Timestamp;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherResponse {

    private Main main;
    private Wind wind;
    private LocalDateTime timestamp;



    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private String city;
        private Double temp;
        private Double pressure;
        private Double humidity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private Double speed;
    }
}
