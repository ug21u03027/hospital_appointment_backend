package com.teame.hospital_appointment_backend.dao;

import com.teame.hospital_appointment_backend.models.entities.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface DoctorScheduleDao extends JpaRepository<DoctorSchedule, Long> {

    List<DoctorSchedule> findByDoctor_DoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
}
