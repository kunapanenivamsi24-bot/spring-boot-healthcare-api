package com.healthcare.api.service;

import com.healthcare.api.dto.PatientRequest;
import com.healthcare.api.dto.PatientResponse;
import com.healthcare.api.exception.ResourceNotFoundException;
import com.healthcare.api.model.Patient;
import com.healthcare.api.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private PatientRequest request() {
        return new PatientRequest(
                "Jane", "Doe", LocalDate.of(1990, 5, 14),
                "Female", "jane.doe@example.com", "555-0100");
    }

    private Patient savedPatient() {
        Patient patient = new Patient(
                "Jane", "Doe", LocalDate.of(1990, 5, 14),
                "Female", "jane.doe@example.com", "555-0100");
        patient.setId(1L);
        return patient;
    }

    @Test
    void createPatient_savesAndReturnsResponse() {
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient());

        PatientResponse response = patientService.createPatient(request());

        assertEquals(1L, response.id());
        assertEquals("Jane", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals("jane.doe@example.com", response.email());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void getPatientById_missingId_throwsResourceNotFoundException() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> patientService.getPatientById(99L));
    }

    @Test
    void listPatients_returnsPagedResponses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> page = new PageImpl<>(List.of(savedPatient()), pageable, 1);
        when(patientRepository.findAll(pageable)).thenReturn(page);

        Page<PatientResponse> result = patientService.listPatients(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Jane", result.getContent().get(0).firstName());
        verify(patientRepository).findAll(pageable);
    }

    @Test
    void updatePatient_missingId_throwsResourceNotFoundException() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> patientService.updatePatient(99L, request()));
    }

    @Test
    void deletePatient_existingId_deletesPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(savedPatient()));

        patientService.deletePatient(1L);

        verify(patientRepository).delete(any(Patient.class));
    }
}
