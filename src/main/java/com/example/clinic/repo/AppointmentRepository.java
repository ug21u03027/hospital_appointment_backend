package com.example.clinic.repo;

import com.example.clinic.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByOrderByDateAscSlotAsc();

    @Query("select (count(a) > 0) from Appointment a where a.doctor.id = :doctorId and a.date = :date and a.slot = :slot")
    boolean existsConflict(@Param("doctorId") Long doctorId, @Param("date") String date, @Param("slot") String slot);

    List<Appointment> findByDoctor_IdAndDateOrderBySlot(Long doctorId, String date);
}
