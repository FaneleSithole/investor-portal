package com.enviro.assessment.junior.fanelesibongesithole.model;

import java.time.LocalDate;

public class Investor {

    private final String id;
    private final String firstName;
    private final String lastName;
    private final String firmName;
    private final LocalDate dateOfBirth;

    public Investor(String id, String firstName, String lastName, String firmName, LocalDate dateOfBirth) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.firmName = firmName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFirmName() { return firmName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }

    public int getAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear()
                - (LocalDate.now().getDayOfYear() < dateOfBirth.getDayOfYear() ? 1 : 0);
    }
}
