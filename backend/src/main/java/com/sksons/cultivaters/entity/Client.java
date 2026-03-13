package com.sksons.cultivaters.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;
    private String village;

    @Column(columnDefinition = "TEXT")
    private String fieldDetails;

    private Double totalCharge = 0.0;
    private Double totalPaid = 0.0;
    private Double pendingBalance = 0.0;

    public Client() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getFieldDetails() { return fieldDetails; }
    public void setFieldDetails(String fieldDetails) { this.fieldDetails = fieldDetails; }

    public Double getTotalCharge() { return totalCharge; }
    public void setTotalCharge(Double totalCharge) { this.totalCharge = totalCharge; }

    public Double getTotalPaid() { return totalPaid; }
    public void setTotalPaid(Double totalPaid) { this.totalPaid = totalPaid; }

    public Double getPendingBalance() { return pendingBalance; }
    public void setPendingBalance(Double pendingBalance) { this.pendingBalance = pendingBalance; }
}
