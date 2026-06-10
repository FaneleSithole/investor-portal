package com.fanele.capitalflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String firmName;

    private String phone;

    @Column(length = 2000)
    private String bio;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'Institutional Investor'")
    private String role = "Institutional Investor";

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean verified = true;

    @Column(columnDefinition = "INT DEFAULT 75")
    private int securityProgress = 75;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean twoFactorEnabled = false;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean notifyPortfolio = true;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean notifyWithdrawals = true;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean notifyCompliance = true;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean notifyReports = true;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean notifyMarketing = false;

    protected UserEntity() {}

    public UserEntity(String email, String passwordHash, String firstName, String lastName,
                      String firmName, LocalDate dateOfBirth) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.firmName = firmName;
        this.dateOfBirth = dateOfBirth;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFirmName() { return firmName; }
    public String getPhone() { return phone; }
    public String getBio() { return bio; }
    public String getRole() {
        return role != null ? role : "Institutional Investor";
    }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isVerified() { return verified; }
    public int getSecurityProgress() { return securityProgress; }
    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public boolean isNotifyPortfolio() { return notifyPortfolio; }
    public boolean isNotifyWithdrawals() { return notifyWithdrawals; }
    public boolean isNotifyCompliance() { return notifyCompliance; }
    public boolean isNotifyReports() { return notifyReports; }
    public boolean isNotifyMarketing() { return notifyMarketing; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirmName(String firmName) { this.firmName = firmName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setBio(String bio) { this.bio = bio; }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }
    public void setSecurityProgress(int securityProgress) { this.securityProgress = securityProgress; }
    public void setNotifyPortfolio(boolean notifyPortfolio) { this.notifyPortfolio = notifyPortfolio; }
    public void setNotifyWithdrawals(boolean notifyWithdrawals) { this.notifyWithdrawals = notifyWithdrawals; }
    public void setNotifyCompliance(boolean notifyCompliance) { this.notifyCompliance = notifyCompliance; }
    public void setNotifyReports(boolean notifyReports) { this.notifyReports = notifyReports; }
    public void setNotifyMarketing(boolean notifyMarketing) { this.notifyMarketing = notifyMarketing; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        LocalDate today = LocalDate.now();
        return today.getYear() - dateOfBirth.getYear()
                - (today.getDayOfYear() < dateOfBirth.getDayOfYear() ? 1 : 0);
    }

    public int getJoinedYear() {
        return createdAt != null ? createdAt.getYear() : LocalDate.now().getYear();
    }
}
