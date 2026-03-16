package com.sksons.cultivaters.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "work_entries")
@TypeAlias("WorkEntry")
public class WorkEntry {

    @Transient
    public static final String SEQUENCE_NAME = "work_entries_sequence";

    @Id
    private Long id;

    @DBRef
    private Client client;

    @DBRef(lazy = true)
    private Vehicle vehicle;

    @DBRef(lazy = true)
    private Equipment equipment;

    @DBRef(lazy = true)
    private Driver driver;

    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer totalMinutes;
    private Double totalCost;
    private Boolean isManualCost = false;
    private Boolean isPaid = false;
    private String notes;

    public WorkEntry() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(Integer totalMinutes) { this.totalMinutes = totalMinutes; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public Boolean getIsManualCost() { return isManualCost; }
    public void setIsManualCost(Boolean isManualCost) { this.isManualCost = isManualCost; }

    public Boolean getIsPaid() { return isPaid; }
    public void setIsPaid(Boolean isPaid) { this.isPaid = isPaid; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
