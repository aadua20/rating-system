package com.leverx.ratingsystem.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String confirmationLink) {
        String subject = "Email Confirmation";
        String message = "<p>Please click the link below to confirm your email:</p>"
                + "<p><a href=\"" + confirmationLink + "\">Confirm Email</a></p>"
                + "<p>If you did not request this, please ignore this email.</p>";

        sendEmail(to, subject, message);
    }

    public void sendPasswordResetEmail(String to, String resetCode) {
        String subject = "Password Reset Request";
        String message = "<p>Use the following code to reset your password:</p>"
                + "<h3>" + resetCode + "</h3>"
                + "<p>This code will expire in 10 minutes.</p>"
                + "<p>If you did not request this, please ignore this email.</p>";

        sendEmail(to, subject, message);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + to + ": " + e.getMessage(), e);
        }
    }
}