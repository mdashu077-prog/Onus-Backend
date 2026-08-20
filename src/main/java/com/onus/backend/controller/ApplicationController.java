package com.onus.backend.controller;

import com.onus.backend.entity.Application;
import com.onus.backend.exception.DuplicateApplicationException;
import com.onus.backend.service.ApplicationService;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService
    ) {
        this.applicationService = applicationService;
    }

    // =====================================================
    // APPLY FOR JOB
    // =====================================================

    @PostMapping(
            value = "/{jobId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Application> apply(
            @PathVariable Long jobId,
            Authentication authentication,

            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String qualification,
            @RequestParam String experience,
            @RequestParam String location,

            @RequestParam(required = false)
            String coverLetter,

            @RequestParam("resume")
            MultipartFile resume
    ) throws IOException {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();

        Application application =
                applicationService.apply(
                        email,
                        jobId,
                        fullName,
                        phone,
                        qualification,
                        experience,
                        location,
                        coverLetter,
                        resume
                );

        return ResponseEntity
                .status(201)
                .body(application);
    }

    // =====================================================
    // CHECK IF ALREADY APPLIED
    // =====================================================

    @GetMapping("/check/{jobId}")
    public ResponseEntity<Map<String, Boolean>> checkApplication(
            @PathVariable Long jobId,
            Authentication authentication
    ) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();

        boolean applied =
                applicationService.hasApplied(
                        email,
                        jobId
                );

        return ResponseEntity.ok(
                Map.of("applied", applied)
        );
    }

    // =====================================================
    // MY APPLICATIONS
    // =====================================================

    @GetMapping("/my")
    public ResponseEntity<List<Application>> getMyApplications(
            Authentication authentication
    ) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();

        return ResponseEntity.ok(
                applicationService.getMyApplications(email)
        );
    }

    // =====================================================
    // GET MY RESUME INFORMATION
    // =====================================================

    @GetMapping("/resume")
    public ResponseEntity<?> getMyResume(
            Authentication authentication
    ) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();

        Application application =
                applicationService.getMyResume(email);

        if (application == null) {
            return ResponseEntity
                    .status(404)
                    .body(
                            Map.of(
                                    "message",
                                    "No resume found"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "fileName",
                        application.getResumeFileName(),
                        "contentType",
                        application.getResumeContentType()
                )
        );
    }

    // =====================================================
    // DOWNLOAD MY RESUME
    // =====================================================

    @GetMapping("/resume/download")
    public ResponseEntity<?> downloadMyResume(
            Authentication authentication
    ) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();

        Application application =
                applicationService.getMyResume(email);

        if (application == null ||
                application.getResumeData() == null) {

            return ResponseEntity
                    .status(404)
                    .body(
                            Map.of(
                                    "message",
                                    "No resume found"
                            )
                    );
        }

        byte[] resumeData =
                application.getResumeData();

        String fileName =
                application.getResumeFileName();

        if (fileName == null ||
                fileName.isBlank()) {

            fileName = "resume.pdf";
        }

        ByteArrayResource resource =
                new ByteArrayResource(resumeData);

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_PDF
        );

        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(fileName)
                        .build()
        );

        headers.setContentLength(
                resumeData.length
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resource);
    }

    // =====================================================
    // VIEW MY RESUME IN BROWSER
    // =====================================================

    @GetMapping("/resume/view")
    public ResponseEntity<?> viewMyResume(
            Authentication authentication
    ) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();

        Application application =
                applicationService.getMyResume(email);

        if (application == null ||
                application.getResumeData() == null) {

            return ResponseEntity
                    .status(404)
                    .body(
                            Map.of(
                                    "message",
                                    "No resume found"
                            )
                    );
        }

        byte[] resumeData =
                application.getResumeData();

        String fileName =
                application.getResumeFileName();

        if (fileName == null ||
                fileName.isBlank()) {

            fileName = "resume.pdf";
        }

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_PDF
        );

        headers.setContentDisposition(
                ContentDisposition
                        .inline()
                        .filename(fileName)
                        .build()
        );

        headers.setContentLength(
                resumeData.length
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(
                        new ByteArrayResource(
                                resumeData
                        )
                );
    }

    // =====================================================
    // DUPLICATE APPLICATION
    // =====================================================

    @ExceptionHandler(
            DuplicateApplicationException.class
    )
    public ResponseEntity<Map<String, String>>
    handleDuplicateApplication(
            DuplicateApplicationException exception
    ) {

        return ResponseEntity
                .status(409)
                .body(
                        Map.of(
                                "message",
                                exception.getMessage()
                        )
                );
    }
}