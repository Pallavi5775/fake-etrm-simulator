package com.trading.ctrm.weather;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    @Query("SELECT w FROM WeatherData w WHERE w.location = :location AND w.date = :date")
    Optional<WeatherData> findByLocationAndDate(@Param("location") String location, @Param("date") LocalDate date);
}
