package com.onus.backend.service;

import com.onus.backend.entity.Application;
import com.onus.backend.entity.Job;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(
            JavaMailSender mailSender
    ) {
        this.mailSender = mailSender;
    }

    public void sendApplicationConfirmation(
            String email,
            String fullName,
            Job job,
            Application application
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "Application Received - "
                        + job.getTitle()
        );

        String company =
                job.getCompany() != null
                        ? job.getCompany()
                        : "ONUS";

        String body =
                "Hello "
                        + fullName
                        + ",\n\n"

                        + "Your job application has been successfully received.\n\n"

                        + "Application Details\n"
                        + "---------------------------\n"
                        + "Job: "
                        + job.getTitle()
                        + "\n"

                        + "Company: "
                        + company
                        + "\n"

                        + "Location: "
                        + (
                        job.getLocation() != null
                                ? job.getLocation()
                                : "Not specified"
                )
                        + "\n"

                        + "Application ID: "
                        + application.getId()
                        + "\n"

                        + "Status: "
                        + application.getStatus()
                        + "\n\n"

                        + "We have received your resume and application details successfully.\n\n"

                        + "Thank you for applying through ONUS.\n\n"

                        + "Regards,\n"
                        + "ONUS Team";

        message.setText(body);

        mailSender.send(message);
    }
}