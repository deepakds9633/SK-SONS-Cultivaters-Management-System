package com.sksons.cultivaters.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;
    private String licenseNumber;
    private String address;



    private Double dailySalary = 0.0;
    private Double totalSalaryEarned = 0.0;
    private Double totalSalaryPaid = 0.0;
    private Double pendingSalary = 0.0;

    @ManyToOne
    @JoinColumn(name = "assigned_vehicle_id")
    private Vehicle assignedVehicle;

    public Vehicle getAssignedVehicle() { return assignedVehicle; }
    public void setAssignedVehicle(Vehicle assignedVehicle) { this.assignedVehicle = assignedVehicle; }

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
}
