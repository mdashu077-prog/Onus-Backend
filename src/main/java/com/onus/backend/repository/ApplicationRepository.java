package com.onus.backend.repository;

import com.onus.backend.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    // =====================================================
    // CHECK DUPLICATE APPLICATION
    // =====================================================

    boolean existsByApplicantEmailAndJobId(
            String applicantEmail,
            Long jobId
    );


    // =====================================================
    // MY APPLICATIONS
    // =====================================================

    List<Application> findByApplicantEmailOrderByAppliedAtDesc(
            String applicantEmail
    );


    // =====================================================
    // FIND APPLICATIONS HAVING RESUME
    // =====================================================

    List<Application>
    findByApplicantEmailAndResumeFileNameIsNotNullOrderByAppliedAtDesc(
            String applicantEmail
    );


    // =====================================================
    // GET LATEST APPLICATION HAVING RESUME
    // =====================================================

    Optional<Application>
    findFirstByApplicantEmailAndResumeFileNameIsNotNullOrderByAppliedAtDesc(
            String applicantEmail
    );
}