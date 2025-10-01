package com.teame.hospital_appointment_backend.dao;

import com.teame.hospital_appointment_backend.models.entities.Appointment;
import com.teame.hospital_appointment_backend.models.entities.Patient;
import com.teame.hospital_appointment_backend.models.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentDao extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctor_DoctorIdOrderByDateDescStartTimeDesc(Long doctorId);

    List<Appointment> findByPatient_PatientIdOrderByDateDescStartTimeDesc(Long patientId);

    List<Appointment> findByDoctor_DoctorIdAndDateAndStartTimeAndEndTimeAndStatusNot(Long doctorId,
                                                                     LocalDate date,
                                                                     LocalTime startTime,
                                                                     LocalTime endTime,
                                                                     AppointmentStatus status);

    List<Appointment> findByDoctor_DoctorIdAndDateAndStatusNot(Long doctorId,
                                                              LocalDate date,
                                                              AppointmentStatus status);

}
