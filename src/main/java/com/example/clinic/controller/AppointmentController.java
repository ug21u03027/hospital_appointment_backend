package com.example.clinic.controller;

import com.example.clinic.model.User;
import com.example.clinic.model.Appointment;
import com.example.clinic.model.Doctor;
import com.example.clinic.repo.AppointmentRepository;
import com.example.clinic.repo.DoctorRepository;
import com.example.clinic.repo.UserRepository;
import com.example.clinic.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final SessionService sessions;
    private final UserRepository userRepo;

    public AppointmentController(AppointmentRepository appointmentRepo, DoctorRepository doctorRepo, SessionService sessions, UserRepository userRepo) {
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
        this.sessions = sessions;
        this.userRepo = userRepo;
    }

    public static class CreateAppointmentRequest {

        public String patientName;
        public Long doctorId;
        public String date; // yyyy-MM-dd
        public String slot; // e.g. "10:00"
    }

    @GetMapping
    public List<Appointment> all() {
        return appointmentRepo.findAllByOrderByDateAscSlotAsc();
    }

    @GetMapping("/my")
    public ResponseEntity<?> myAppointments(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        if (!sessions.isValid(token)) {
            return ResponseEntity.status(401).body("Login required");
        }
        String username = sessions.getUsername(token);
        var userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid session");
        }
        var user = userOpt.get();
        if (user.getRole() == User.Role.DOCTOR) {
            return ResponseEntity.ok(appointmentRepo.findAllByOrderByDateAscSlotAsc().stream().filter(a -> a.getDoctor().getId().equals((doctorRepo.findByUser_Id(user.getId())).get().getId())).toList());
        } else {
            return ResponseEntity.ok(appointmentRepo.findAllByOrderByDateAscSlotAsc().stream().filter(a -> a.getUser().getId().equals(user.getId())).toList());
        }
    }

@GetMapping("/slots/{doctorId}/{date}")
public ResponseEntity<?> slots(@PathVariable Long doctorId, @PathVariable String date) {
    try {
        String[] slots = {"10:00", "11:00", "12:00", "14:30", "15:30", "16:30"};
        List<Appointment> booked = appointmentRepo.findByDoctor_IdAndDateOrderBySlot(doctorId, date);
        java.util.Set<String> taken = booked.stream()
                .map(Appointment::getSlot)
                .collect(java.util.stream.Collectors.toSet());
        System.out.print(taken);
        java.util.List<String> avail = new java.util.ArrayList<>();
        for (String s : slots) {
            System.out.print(s);
            if (!taken.contains(s)) {
                avail.add(s);
            }
        }
        return ResponseEntity.ok(avail);
    } catch (Exception e) {
        e.printStackTrace(); // Log full error in backend console
        return ResponseEntity.status(500).body("Error fetching slots: " + e.getMessage());
    }
}

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(value = "X-Auth-Token", required = false) String token,
            @RequestBody CreateAppointmentRequest req) {
        if (!sessions.isValid(token)) {
            return ResponseEntity.status(401).body("Login required");
        }
        if (req.patientName == null || req.patientName.isBlank() || req.doctorId == null || req.date == null || req.slot == null) {
            return ResponseEntity.badRequest().body("Missing fields");
        }
        Optional<Doctor> docOpt = doctorRepo.findById(req.doctorId);
        if (docOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Doctor not found");
        }

        String username = sessions.getUsername(token);
        var userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid session");
        }

        // Conflict check for same doctor, date & slot
        if (appointmentRepo.existsConflict(req.doctorId, req.date, req.slot)) {
            return ResponseEntity.badRequest().body("Slot already taken for this doctor");
        }

        Appointment appt = new Appointment(req.patientName, req.date, req.slot, docOpt.get(),userOpt.get());
        return ResponseEntity.ok(appointmentRepo.save(appt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long id) {
        if (!sessions.isValid(token)) {
            return ResponseEntity.status(401).body("Login required");
        }
        if (!appointmentRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        appointmentRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
