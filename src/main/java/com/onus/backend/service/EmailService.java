package com.onus.backend.service;

import com.onus.backend.entity.Application;
import com.onus.backend.entity.Job;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public EmailService() {
        // Email temporarily disabled.
        // JavaMailSender ki zarurat nahi hai.
    }

    public void sendApplicationConfirmation(
            String email,
            String fullName,
            Job job,
            Application application
    ) {
        // Email temporarily disabled.
        // Baad mein Gmail/SMTP configure karke yahan
        // email sending logic wapas add karenge.
    }
}