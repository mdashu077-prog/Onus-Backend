package com.onus.backend.controller;

import com.onus.backend.entity.Job;
import com.onus.backend.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // =====================================================
    // CREATE JOB
    // =====================================================

    @PostMapping
    public ResponseEntity<Job> createJob(
            @RequestBody Job job
    ) {
        return ResponseEntity
                .status(201)
                .body(jobService.createJob(job));
    }

    // =====================================================
    // GET ALL JOBS
    // PUBLIC
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(
                jobService.getAllJobs()
        );
    }

    // =====================================================
    // GET JOB BY ID
    // PUBLIC
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                jobService.getJobById(id)
        );
    }

    // =====================================================
    // DELETE JOB
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id
    ) {
        jobService.deleteJob(id);

        return ResponseEntity.noContent().build();
    }
}