package com.sksons.cultivaters.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "attendance")
@TypeAlias("Attendance")
public class Attendance {

    @Transient
    public static final String SEQUENCE_NAME = "attendance_sequence";

    @Id
    private Long id;

    @DBRef
    private Driver driver;

    private LocalDate date;
    private Boolean present = false;
    private Double salaryForDay = 0.0;
    private Double paidAmount = 0.0;
    private Double pendingAmount = 0.0;
    private String remarks;

    public Attendance() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Boolean getPresent() { return present; }
    public void setPresent(Boolean present) { this.present = present; }

    public Double getSalaryForDay() { return salaryForDay; }
    public void setSalaryForDay(Double salaryForDay) { this.salaryForDay = salaryForDay; }

    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }

    public Double getPendingAmount() { return pendingAmount; }
    public void setPendingAmount(Double pendingAmount) { this.pendingAmount = pendingAmount; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
