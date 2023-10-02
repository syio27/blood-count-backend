package com.pja.bloodcount.constant;

import com.pja.bloodcount.dto.request.InviteUserRequest;
import com.pja.bloodcount.model.Group;

public class MailMessageConstants {
    public static String getInviteMessage(InviteUserRequest inviteRequest, String generatedPassword, Group group) {
        return """
                Dear new User<br><br>

                You have been invited to join our application as a %s.<br><br>

                Here are your login credentials:<br>
                Email: %s<br>
                Temporary password: %s<br>
                Assigned Group: %s<br>
                Group Type: %s<br><br>

                Please log in using these details. We strongly recommend that you change your password immediately after your first login for security purposes.<br>
                If you have any issues, please feel free to contact us.<br>
                (*Please use the user icon in the top right corner)<br><br>
                Best regards,<br>
                The Application Team""".formatted(inviteRequest.getRole().toString().toLowerCase(), inviteRequest.getEmail(), generatedPassword, group.getGroupNumber(), group.getGroupType());
    }

    public static String getBanMessage(String email) {
        return """
                Dear Admin User

                We hope this message finds you well.
                We regret to inform you that your account with %s has been temporarily suspended, effective immediately.

                Best regards
                The Application Team""".formatted(email);
    }


    public static String getUnbanMessage() {
        return """
                Dear Admin User

                We are pleased to inform you that the suspension on your account has been lifted.
                Your account is activated.
                Best regards,
                The Application Team""";
    }

    public static String getGameCompleteMessage(String gameHistoryUrl) {
        return """
                Dear User

                Your game has been auto complete, and scored
                You can view you game result by below link
                Games History page: %s

                Best regards,
                The Application Team""".formatted(gameHistoryUrl);
    }

    public static String getForgotPasswordMessage(String resetUrl) {
        return """
                Dear User,

                We received a request to reset your password for your BloodCount app account
                If you didn't make this request, you can safely ignore this email
                Your password won't be changed until you create a new one using the link below.

                To reset your password, please click the following link:
                %s
                
                This link will expire in 3 hours. If you don't reset your password within this time frame, you'll need to submit a new request.

                Best regards,
                The Your BloodCount app Team""".formatted(resetUrl);
    }

    public static String getResetPasswordMessage(String formattedDateTime, String loginUrl) {
        return """
                Dear User,

                You password has been reset at %s
                and you can access the login page and enter to app with new credentials here ->
                
                %s
                
                Best regards,
                The Your BloodCount app Team""".formatted(formattedDateTime, loginUrl);
    }
}

