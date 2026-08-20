package com.onus.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_applicant_job",
                        columnNames = {
                                "applicant_email",
                                "job_id"
                        }
                )
        }
)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "applicant_email",
            nullable = false
    )
    private String applicantEmail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "job_id",
            nullable = false
    )
    private Job job;

    @Column(nullable = false)
    private String status = "APPLIED";

    @Column(nullable = false)
    private Instant appliedAt = Instant.now();

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String qualification;

    @Column(nullable = false)
    private String experience;

    @Column(nullable = false)
    private String location;

    @Column(length = 2000)
    private String coverLetter;

    private String resumeFileName;

    private String resumeContentType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] resumeData;

    public Application() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(
            String applicantEmail
    ) {
        this.applicantEmail =
                applicantEmail;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(
            Instant appliedAt
    ) {
        this.appliedAt = appliedAt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(
            String fullName
    ) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone
    ) {
        this.phone = phone;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(
            String qualification
    ) {
        this.qualification =
                qualification;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(
            String experience
    ) {
        this.experience =
                experience;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(
            String location
    ) {
        this.location = location;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(
            String coverLetter
    ) {
        this.coverLetter =
                coverLetter;
    }

    public String getResumeFileName() {
        return resumeFileName;
    }

    public void setResumeFileName(
            String resumeFileName
    ) {
        this.resumeFileName =
                resumeFileName;
    }

    public String getResumeContentType() {
        return resumeContentType;
    }

    public void setResumeContentType(
            String resumeContentType
    ) {
        this.resumeContentType =
                resumeContentType;
    }

    public byte[] getResumeData() {
        return resumeData;
    }

    public void setResumeData(
            byte[] resumeData
    ) {
        this.resumeData = resumeData;
    }
}