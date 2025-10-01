package com.teame.hospital_appointment_backend.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotResponse {
    private Long doctorId;
    private LocalDate date;
    private List<List<LocalTime>> availableSlots;
    private List<List<LocalTime>> bookedSlots;
}
