package com.techcorp.model;

import java.util.Objects;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class Department {
    private Long id;

    @NotBlank(message = "Nazwa departamentu jest wymagana")
    private String name;

    @NotBlank(message = "Lokalizacja jest wymagana")
    private String location;

    @PositiveOrZero(message = "Budżet nie może być ujemny")
    private double budget;

    @Email(message = "Nieprawidłowy format email managera")
    private String managerEmail;

    public Department() {
    }

    public Department(Long id, String name, String location, double budget, String managerEmail) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
        this.location = Objects.requireNonNull(location, "location");
        this.budget = budget;
        this.managerEmail = managerEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = Objects.requireNonNull(location, "location");
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        if (budget < 0) throw new IllegalArgumentException("budget cannot be negative");
        this.budget = budget;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }
}
