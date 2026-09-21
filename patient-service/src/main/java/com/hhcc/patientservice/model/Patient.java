package com.hhcc.patientservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "patients", uniqueConstraints = {@UniqueConstraint(name = "uk_patient_user_id", columnNames = "user_id"),
                @UniqueConstraint(name = "uk_patient_mobile_number", columnNames = "mobile_number")})
public class Patient {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id",nullable=false,unique=true,length=150)
    private String userId;

    @Column(name="first_name",nullable=false,length=50)
    private String firstName;

    @Column(name="last_name",nullable=false,length=50)
    private String lastName;

    @Column(name="mobile_number",nullable=false,length=15)
    private String mobileNumber;

    @Column(name="date_of_birth",nullable=false)
    private LocalDate dateOfBirth;

    @Column(nullable=false,length=20)
    private String gender;

    @Column(nullable=false)
    private Boolean active=true;

    @Column(name="created_at",nullable=false,updatable=false)
    private LocalDateTime createdAt;

    @Column(name="updated_at",nullable=false)
    private LocalDateTime updatedAt;

    @PrePersist
    private void onCreate(){
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    private void onUpdate(){
        updatedAt=LocalDateTime.now();
    }



}
