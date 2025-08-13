package com.example.clinic.model;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import jakarta.persistence.*;

@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialty;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Doctor() {
    }

    public Doctor(String name, String specialty, User user) {
        this.name = name;
        this.specialty = specialty;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
