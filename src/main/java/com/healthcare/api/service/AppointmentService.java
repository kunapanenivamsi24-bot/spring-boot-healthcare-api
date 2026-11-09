package com.healthcare.api.service;

import com.healthcare.api.dto.AppointmentRequest;
import com.healthcare.api.dto.AppointmentResponse;
import com.healthcare.api.exception.ResourceNotFoundException;
import com.healthcare.api.model.Appointment;
import com.healthcare.api.model.AppointmentStatus;
import com.healthcare.api.model.Patient;
import com.healthcare.api.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientService patientService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
    }

    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Patient patient = patientService.findPatientOrThrow(request.patientId());
        Appointment appointment = new Appointment(
                patient,
                request.doctorName(),
                request.appointmentTime(),
                request.reason(),
                request.status() == null ? AppointmentStatus.SCHEDULED : request.status()
        );
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listAppointmentsByPatient(Long patientId) {
        // Fail fast with 404 if the patient does not exist.
        patientService.findPatientOrThrow(patientId);
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        return AppointmentResponse.from(findAppointmentOrThrow(id));
    }

    public AppointmentResponse cancelAppointment(Long id) {
        Appointment appointment = findAppointmentOrThrow(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    private Appointment findAppointmentOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.appointment(id));
    }
}
