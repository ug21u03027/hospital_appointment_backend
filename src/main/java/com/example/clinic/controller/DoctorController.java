package com.example.clinic.controller;

import com.example.clinic.model.Doctor;
import com.example.clinic.repo.DoctorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository repo;

    public DoctorController(DoctorRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Doctor> all() {
        return repo.findAll();
    }

    @PostMapping
    public Doctor create(@RequestBody Doctor d) {
        return repo.save(d);
    }
}
