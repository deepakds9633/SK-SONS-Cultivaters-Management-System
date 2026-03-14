package com.sksons.cultivaters.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "equipment")
public class Equipment {

    @Transient
    public static final String SEQUENCE_NAME = "equipment_sequence";

    @Id
    private Long id;

    private String name;
    private Double hourlyCharge = 0.0;

    public Equipment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getHourlyCharge() { return hourlyCharge; }
    public void setHourlyCharge(Double hourlyCharge) { this.hourlyCharge = hourlyCharge; }
}
