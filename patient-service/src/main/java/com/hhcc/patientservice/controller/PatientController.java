package com.hhcc.patientservice.controller;

import com.hhcc.patientservice.dto.CreatePatientRequest;
import com.hhcc.patientservice.dto.PatientResponse;
import com.hhcc.patientservice.dto.UpdatePatientRequest;
import com.hhcc.patientservice.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/patients")
@RestController
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest request, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        PatientResponse response = patientService.createPatient(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/me")
    public ResponseEntity<PatientResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();

        PatientResponse response = patientService.getMyProfile(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {

        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PutMapping("/me")
    public ResponseEntity<PatientResponse> updateMyProfile(@Valid @RequestBody UpdatePatientRequest request, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        PatientResponse response = patientService.updateMyProfile(request, userId);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateMyProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        patientService.deactivateMyProfile(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientResponse> activatePatient(@PathVariable Long id) {
        PatientResponse response = patientService.activatePatient(id);
        return ResponseEntity.ok(response);
    }


}
