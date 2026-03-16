package com.sksons.cultivaters.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clients")
@TypeAlias("Client")
public class Client {

    @Transient
    public static final String SEQUENCE_NAME = "clients_sequence";

    @Id
    private Long id;

    private String name;
    private String phone;
    private String village;
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
