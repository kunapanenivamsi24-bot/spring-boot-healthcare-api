package com.healthcare.api.dto;

import com.healthcare.api.model.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponse(
        Long id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String email,
        String phone,
        LocalDateTime createdAt
) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getCreatedAt()
        );
    }
}
