package com.healthcare.api.service;

import com.healthcare.api.dto.PatientRequest;
import com.healthcare.api.dto.PatientResponse;
import com.healthcare.api.exception.ResourceNotFoundException;
import com.healthcare.api.model.Patient;
import com.healthcare.api.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientResponse createPatient(PatientRequest request) {
        Patient patient = new Patient(
                request.firstName(),
                request.lastName(),
                request.dateOfBirth(),
                request.gender(),
                request.email(),
                request.phone()
        );
        return PatientResponse.from(patientRepository.save(patient));
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        return PatientResponse.from(findPatientOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> listPatients(Pageable pageable) {
        return patientRepository.findAll(pageable).map(PatientResponse::from);
    }

    public PatientResponse updatePatient(Long id, PatientRequest request) {
        Patient patient = findPatientOrThrow(id);
        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setEmail(request.email());
        patient.setPhone(request.phone());
        return PatientResponse.from(patientRepository.save(patient));
    }

    public void deletePatient(Long id) {
        Patient patient = findPatientOrThrow(id);
        patientRepository.delete(patient);
    }

    @Transactional(readOnly = true)
    public Patient findPatientOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.patient(id));
    }
}
