package com.sksons.cultivaters.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "enquiries")
@TypeAlias("Enquiry")
public class Enquiry {
    
    @Id
    private String id;
    
    private String name;
    private String phone;
    private String village;
    private String message;
    private LocalDateTime submissionDate;

    public Enquiry() {
        this.submissionDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }
}
