package com.enviro.assessment.junior.fanelesibongesithole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "reports")
public class ReportEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private LocalDate generatedDate;

    @Column(nullable = false)
    private long fileSizeBytes;

    protected ReportEntity() {}

    public ReportEntity(String id, String title, String description, String type,
                        LocalDate generatedDate, long fileSizeBytes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.generatedDate = generatedDate;
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public LocalDate getGeneratedDate() { return generatedDate; }
    public long getFileSizeBytes() { return fileSizeBytes; }
}
