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
                Dear Admin User<br><br>

                We hope this message finds you well.<br>
                We regret to inform you that your account with %s has been temporarily suspended, effective immediately.<br>

                Best regards<br>
                The Application Team""".formatted(email);
    }


    public static String getUnbanMessage() {
        return """
                Dear Admin User<br><br>

                We are pleased to inform you that the suspension on your account has been lifted.<br>
                Your account is activated.<br>
                Best regards,<br>
                The Application Team""";
    }

    public static String getGameCompleteMessage() {
        return """
                Dear User<br><br>

                Your game has been auto complete, and scored<br>
                You can view you game result by clicking the button<br><br>

                Best regards,<br>
                The Application Team""";
    }

    public static String getForgotPasswordMessage() {
        return """
                Dear User,<br><br>

                We received a request to reset your password for your BloodCount app account<br>
                If you didn't make this request, you can safely ignore this email<br>
                Your password won't be changed until you create a new one by clicking the button.<br><br>
                
                The link will expire in 3 hours. If you don't reset your password within this time frame, you'll need to submit a new request.<br><br>

                Best regards,<br>
                The Your BloodCount app Team""";
    }

    public static String getResetPasswordMessage(String formattedDateTime) {
        return """
                Dear User,<br><br>

                You password has been reset at %s<br>
                and you can access the login page and enter to app with new credentials<br><br>
                
                Best regards,<br>
                The Your BloodCount app Team""".formatted(formattedDateTime);
    }
}

