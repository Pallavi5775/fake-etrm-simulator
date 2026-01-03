package com.trading.ctrm.pricecurve;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "price_curve")
public class PriceCurve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String curveName;
    private LocalDate date;
    private Double price;
    // Add more fields as needed

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCurveName() { return curveName; }
    public void setCurveName(String curveName) { this.curveName = curveName; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
