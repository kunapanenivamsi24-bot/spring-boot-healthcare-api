package com.healthcare.api.dto;

import com.healthcare.api.model.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull Long patientId,
        @NotBlank @Size(max = 100) String doctorName,
        @NotNull @Future LocalDateTime appointmentTime,
        @Size(max = 500) String reason,
        AppointmentStatus status
) {
}
