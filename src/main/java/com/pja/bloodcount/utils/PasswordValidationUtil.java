package com.pja.bloodcount.utils;

import com.pja.bloodcount.dto.request.PasswordChangeDTO;
import com.pja.bloodcount.exceptions.IncorrectOldPasswordException;
import com.pja.bloodcount.exceptions.PasswordRepeatException;
import com.pja.bloodcount.exceptions.PasswordSameAsOldException;
import com.pja.bloodcount.exceptions.PasswordValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordValidationUtil {

    private PasswordValidationUtil() {
        // private constructor
    }

    public static void validatePasswordOrThrowExceptions(PasswordChangeDTO passwordChangeDTO, String password, PasswordEncoder passwordEncoder) {
        if (!CredentialValidationUtil.validatePassword(passwordChangeDTO.getNewPassword())) {
            throw new PasswordValidationException("Password is not valid, doesnt match regex rule");
        }

        if (isPreviousPasswordCorrect(passwordChangeDTO, password, passwordEncoder)) {
            throw new IncorrectOldPasswordException("Old password is incorrect");
        }

        if (isNewPasswordNotSameAsPrevious(passwordChangeDTO)) {
            throw new PasswordSameAsOldException("New password cannot be the same as old password");
        }

        if (isPasswordConfirmed(passwordChangeDTO)) {
            throw new PasswordRepeatException("New password and new password confirmation are not the same");
        }
    }

    private static boolean isPasswordConfirmed(PasswordChangeDTO passwordChangeDTO) {
        return !passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getNewPasswordRepeat());
    }

    private static boolean isNewPasswordNotSameAsPrevious(PasswordChangeDTO passwordChangeDTO) {
        return passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getOldPassword());
    }

    private static boolean isPreviousPasswordCorrect(PasswordChangeDTO passwordChangeDTO, String password, PasswordEncoder passwordEncoder) {
        return !passwordEncoder.matches(passwordChangeDTO.getOldPassword(), password);
    }
}
