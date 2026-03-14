package com.sksons.cultivaters.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "drivers")
public class Driver {

    @Transient
    public static final String SEQUENCE_NAME = "drivers_sequence";

    @Id
    private Long id;

    private String name;
    private String phone;
    private String licenseNumber;
    private String address;

    private Double dailySalary = 0.0;
    private Double totalSalaryEarned = 0.0;
    private Double totalSalaryPaid = 0.0;
    private Double pendingSalary = 0.0;

    @DBRef(lazy = true)
    private Vehicle assignedVehicle;

    public Driver() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getDailySalary() { return dailySalary; }
    public void setDailySalary(Double dailySalary) { this.dailySalary = dailySalary; }

    public Double getTotalSalaryEarned() { return totalSalaryEarned; }
    public void setTotalSalaryEarned(Double totalSalaryEarned) { this.totalSalaryEarned = totalSalaryEarned; }

    public Double getTotalSalaryPaid() { return totalSalaryPaid; }
    public void setTotalSalaryPaid(Double totalSalaryPaid) { this.totalSalaryPaid = totalSalaryPaid; }

    public Double getPendingSalary() { return pendingSalary; }
    public void setPendingSalary(Double pendingSalary) { this.pendingSalary = pendingSalary; }

    public Vehicle getAssignedVehicle() { return assignedVehicle; }
    public void setAssignedVehicle(Vehicle assignedVehicle) { this.assignedVehicle = assignedVehicle; }
}
