package com.leverx.ratingsystem.util;

public class EmailTemplates {

    public static final String CONFIRMATION_SUBJECT = "Email Confirmation";
    public static final String RESET_SUBJECT = "Password Reset Request";

    public static String buildConfirmationBody(String confirmationLink) {
        return """
                <p>Please click the link below to confirm your email:</p>
                <p><a href="%s">Confirm Email</a></p>
                <p>If you did not request this, please ignore this email.</p>
                """.formatted(confirmationLink);
    }

    public static String buildResetBody(String resetCode) {
        return """
                <p>Use the following code to reset your password:</p>
                <h3>%s</h3>
                <p>This code will expire in 10 minutes.</p>
                <p>If you did not request this, please ignore this email.</p>
                """.formatted(resetCode);
    }
}
