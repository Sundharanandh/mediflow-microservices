package com.hhcc.patientservice.service;

import com.hhcc.patientservice.dto.CreatePatientRequest;
import com.hhcc.patientservice.dto.PatientResponse;
import com.hhcc.patientservice.dto.UpdatePatientRequest;
import com.hhcc.patientservice.exception.PatientAlreadyExistsException;
import com.hhcc.patientservice.exception.PatientNotFoundException;
import com.hhcc.patientservice.model.Patient;
import com.hhcc.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request, String userId) {
        if (patientRepository.existsByUserId(userId)) {
            throw new PatientAlreadyExistsException("Patient profile already exists");
        }
        if (patientRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new PatientAlreadyExistsException("Mobile number already registered");
        }
        Patient patient = Patient.builder().userId(userId).firstName(request.getFirstName()).lastName(request.getLastName()).mobileNumber(request.getMobileNumber()).dateOfBirth(request.getDateOfBirth()).gender(request.getGender()).active(true).build();
        Patient savedPatient = patientRepository.save(patient);
        return mapToResponse(savedPatient);
    }

    @Transactional(readOnly = true)
    public PatientResponse getMyProfile(String userId) {
        Patient patient = patientRepository.findByUserId(userId).orElseThrow(() -> new PatientNotFoundException("Patient profile not found"));
        return mapToResponse(patient);
    }
    // =========================================================
    // GET PATIENT BY ID
    // ADMIN
    // =========================================================


    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        return mapToResponse(patient);
    }

    // =========================================================
    // GET ALL PATIENTS
    // ADMIN
    // =========================================================

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream().map(this::mapToResponse).toList();
    }
    // =========================================================
    // UPDATE MY PROFILE
    // =========================================================

    @Transactional
    public PatientResponse updateMyProfile(UpdatePatientRequest request, String userId) {
        Patient patient = patientRepository.findByUserId(userId).orElseThrow(() -> new PatientNotFoundException("Patient profile not found"));
        if (patientRepository.existsByMobileNumberAndIdNot(request.getMobileNumber(), patient.getId())) {
            throw new PatientAlreadyExistsException("Mobile number already registered");
        }
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setMobileNumber(request.getMobileNumber());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        Patient updatedPatient = patientRepository.save(patient);
        return mapToResponse(updatedPatient);
    }


    // =========================================================
    // DEACTIVATE MY PROFILE
    // =========================================================

    @Transactional
    public void deactivateMyProfile(String userId) {

        Patient patient = patientRepository.findByUserId(userId).orElseThrow(() -> new PatientNotFoundException("Patient profile not found"));

        patient.setActive(false);

        patientRepository.save(patient);
    }


    // =========================================================
    // ACTIVATE PATIENT
    // ADMIN
    // =========================================================

    @Transactional
    public PatientResponse activatePatient(Long id) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        patient.setActive(true);
        Patient updatedPatient = patientRepository.save(patient);
        return mapToResponse(updatedPatient);
    }


    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

    private PatientResponse mapToResponse(Patient patient) {
        return PatientResponse.builder().id(patient.getId()).firstName(patient.getFirstName()).lastName(patient.getLastName()).mobileNumber(patient.getMobileNumber()).dateOfBirth(patient.getDateOfBirth()).gender(patient.getGender()).active(patient.getActive()).createdAt(patient.getCreatedAt()).updatedAt(patient.getUpdatedAt()).build();
    }


}
