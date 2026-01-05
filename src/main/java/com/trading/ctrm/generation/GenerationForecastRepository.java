package com.trading.ctrm.generation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface GenerationForecastRepository extends JpaRepository<GenerationForecast, Long> {

    @Query("SELECT g FROM GenerationForecast g WHERE g.plantName = :plantName AND g.date = :date")
    Optional<GenerationForecast> findByPlantNameAndDate(@Param("plantName") String plantName, @Param("date") LocalDate date);
}
