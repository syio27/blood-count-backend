package com.pja.bloodcount.utils;

import org.springframework.stereotype.Service;

/**
 * ValidationUtil class is used to validate user password and user email
 * @author baglan
 * @version 1.0
 */
@Service
public class ValidationUtil {

    /**
     * Password regex pattern
     * ^                 # start-of-string
     * (?=.*[0-9])       # a digit must occur at least once
     * (?=.*[a-z])       # a lower case letter must occur at least once
     * (?=.*[A-Z])       # an upper case letter must occur at least once
     * (?=.*[@#$%^&+=])  # a special character must occur at least once
     * (?=\S+$)          # no whitespace allowed in the entire string
     * .{8,}             # anything, at least eight places though
     * $                 # end-of-string
     */
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    /**
     * Email regex pattern
     * ^                 # start-of-string
     * [A-Za-z0-9+_.-]+  # must start with string in the bracket [ ], must contains one or more (+)
     * @                 # must contains a '@' symbol
     * (.+)              # domain name with more than one character
     * $                 # end-of-string
     */
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public static boolean validatePassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }
    public static boolean validateEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }
}
