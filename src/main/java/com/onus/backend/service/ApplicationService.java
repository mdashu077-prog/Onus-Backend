package com.onus.backend.service;

import com.onus.backend.entity.Application;
import com.onus.backend.entity.Job;
import com.onus.backend.exception.DuplicateApplicationException;
import com.onus.backend.repository.ApplicationRepository;
import com.onus.backend.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            JobRepository jobRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
    }

    // =====================================================
    // APPLY FOR JOB
    // =====================================================

    public Application apply(
            String email,
            Long jobId,
            String fullName,
            String phone,
            String qualification,
            String experience,
            String location,
            String coverLetter,
            MultipartFile resume
    ) throws IOException {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found")
                );

        // =====================================================
        // DUPLICATE APPLICATION
        // =====================================================

        if (applicationRepository
                .existsByApplicantEmailAndJobId(
                        email,
                        jobId
                )) {

            throw new DuplicateApplicationException(
                    "You have already applied for this job"
            );
        }

        // =====================================================
        // RESUME VALIDATION
        // =====================================================

        if (resume == null || resume.isEmpty()) {

            throw new RuntimeException(
                    "Please upload your resume"
            );
        }

        String contentType =
                resume.getContentType();

        if (!"application/pdf"
                .equalsIgnoreCase(contentType)) {

            throw new RuntimeException(
                    "Resume must be a PDF file"
            );
        }

        // =====================================================
        // CREATE APPLICATION
        // =====================================================

        Application application =
                new Application();

        application.setApplicantEmail(email);
        application.setJob(job);

        application.setFullName(fullName);
        application.setPhone(phone);
        application.setQualification(qualification);
        application.setExperience(experience);
        application.setLocation(location);
        application.setCoverLetter(coverLetter);

        application.setStatus("APPLIED");

        // =====================================================
        // RESUME DATA
        // =====================================================

        application.setResumeFileName(
                resume.getOriginalFilename()
        );

        application.setResumeContentType(
                resume.getContentType()
        );

        application.setResumeData(
                resume.getBytes()
        );

        return applicationRepository.save(
                application
        );
    }

    // =====================================================
    // CHECK APPLICATION STATUS
    // =====================================================

    public boolean hasApplied(
            String email,
            Long jobId
    ) {

        return applicationRepository
                .existsByApplicantEmailAndJobId(
                        email,
                        jobId
                );
    }

    // =====================================================
    // MY APPLICATIONS
    // =====================================================

    public List<Application> getMyApplications(
            String email
    ) {

        return applicationRepository
                .findByApplicantEmailOrderByAppliedAtDesc(
                        email
                );
    }

    // =====================================================
    // GET MY RESUME
    // =====================================================

    public Application getMyResume(
            String email
    ) {

        return applicationRepository
                .findFirstByApplicantEmailAndResumeFileNameIsNotNullOrderByAppliedAtDesc(
                        email
                )
                .orElse(null);
    }

    // =====================================================
    // GET RESUME OR THROW ERROR
    // =====================================================

    public Application requireMyResume(
            String email
    ) {

        return applicationRepository
                .findFirstByApplicantEmailAndResumeFileNameIsNotNullOrderByAppliedAtDesc(
                        email
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "No resume found"
                        )
                );
    }
}