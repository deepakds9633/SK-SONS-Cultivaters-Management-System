package com.sksons.cultivaters.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String modelNumber;
    private LocalDate serviceDate;
    
    @Column(nullable = false)
    private Double hourlyCharge = 0.0;
    
    @Column(nullable = false)
    private Double minuteCharge = 0.0;
    
    private String vehicleType; // ROTAVATOR, CULTIVATOR, HALFCAGE, LOADER
    
    private String imagePath; // Path or URL to the uploaded image

    public Vehicle() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getModelNumber() { return modelNumber; }
    public void setModelNumber(String modelNumber) { this.modelNumber = modelNumber; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public Double getHourlyCharge() { return hourlyCharge; }
    public void setHourlyCharge(Double hourlyCharge) { this.hourlyCharge = hourlyCharge; }

    public Double getMinuteCharge() { return minuteCharge; }
    public void setMinuteCharge(Double minuteCharge) { this.minuteCharge = minuteCharge; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
