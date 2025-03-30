package com.leverx.ratingsystem.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String confirmationLink) {
        String subject = EmailTemplates.CONFIRMATION_SUBJECT;
        String content = EmailTemplates.buildConfirmationBody(confirmationLink);

        sendEmail(to, subject, content);
    }

    public void sendPasswordResetEmail(String to, String resetCode) {
        String subject = EmailTemplates.RESET_SUBJECT;
        String content = EmailTemplates.buildResetBody(resetCode);

        sendEmail(to, subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }
}