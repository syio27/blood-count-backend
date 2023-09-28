package com.pja.bloodcount.constant;

import com.pja.bloodcount.dto.request.InviteUserRequest;
import com.pja.bloodcount.model.Group;

public class MailMessageConstants {
    public static String getInviteMessage(InviteUserRequest inviteRequest, String generatedPassword, Group group, String profileUrl) {
        return "Dear new User\n\n"
                + "You have been invited to join our application as a " + inviteRequest.getRole().toString().toLowerCase() + ".\n\n"
                + "Here are your login credentials:\n"
                + "Email: " + inviteRequest.getEmail() + "\n"
                + "Temporary password: " + generatedPassword + "\n"
                + "Assigned Group: " + group.getGroupNumber() + "\n"
                + "Group Type: " + group.getGroupType() + "\n\n"
                + "Profile page URL: " + profileUrl + "\n\n"
                + "Please log in using these details. We strongly recommend that you change your password immediately after your first login for security purposes.\n\n"
                + "If you have any issues, please feel free to contact us.\n\n"
                + "(Please use the user icon in the top right corner)"
                + "Best regards,\n"
                + "The Application Team";
    }

    public static String getBanMessage(String email) {
        return "Dear Admin User\n\n"
                + "We hope this message finds you well. "
                + "\n"
                + "We regret to inform you that your account with " + email + " has been temporarily suspended, effective immediately."
                + "\n"
                + "Best regards,\n"
                + "The Application Team";
    }


    public static String getUnbanMessage(String email) {
        return "Dear Admin User\n\n"
                + "We are pleased to inform you that the suspension on your account has been lifted."
                + "\n"
                + "Your account is activated."
                + "\n"
                + "Best regards,\n"
                + "The Application Team";
    }

    public static String getGameCompleteMessage(String gameHistoryUrl) {
        return "Dear User\n\n"
                + "Your game has been auto complete, and scored\n"
                + "You can view you game result by below link\n"
                + "Games History page: " + gameHistoryUrl + "\n\n"
                + "Best regards,\n"
                + "The Application Team";
    }

    public static String getForgotPasswordMessage(String resetUrl) {
        return "Dear User,\n" +
                "\n" +
                "We received a request to reset your password for your BloodCount app account. " +
                "If you didn't make this request, you can safely ignore this email. " +
                "Your password won't be changed until you create a new one using the link below.\n" +
                "\n" +
                "To reset your password, please click the following link:\n" +
                "\n" +
                resetUrl +
                "\n" +
                "This link will expire in 3 hours. If you don't reset your password within this time frame, you'll need to submit a new request.\n" +
                "\n" +
                "Best regards,\n" +
                "The Your BloodCount app Team";
    }

    public static String getResetPasswordMessage(String formattedDateTime, String loginUrl) {
        return "Dear User,\n" +
                "\n" +
                "You password has been reset at " + formattedDateTime + "\n" +
                "and you can access the login page and enter to app with new credentials here ->" +
                "\n" +
                loginUrl +
                "\n" +
                "Best regards,\n" +
                "The Your BloodCount app Team";
    }
}

