package com.hhcc.patientservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PatientResponse {
    private Long id;

    private String firstName;

    private String lastName;

    private String mobileNumber;

    private LocalDate dateOfBirth;

    private String gender;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
