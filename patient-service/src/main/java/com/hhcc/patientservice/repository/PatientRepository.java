package com.hhcc.patientservice.repository;

import com.hhcc.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUserId(String userId);

    boolean existsByUserId(String userId);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByMobileNumberAndIdNot(String mobileNumber, Long id);
}