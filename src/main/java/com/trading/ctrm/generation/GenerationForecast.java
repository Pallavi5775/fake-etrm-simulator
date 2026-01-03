package com.trading.ctrm.generation;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "generation_forecast")
public class GenerationForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String plantName;
    private LocalDate date;
    private Double forecastMWh;
    // Add more fields as needed

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlantName() { return plantName; }
    public void setPlantName(String plantName) { this.plantName = plantName; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Double getForecastMWh() { return forecastMWh; }
    public void setForecastMWh(Double forecastMWh) { this.forecastMWh = forecastMWh; }
}
