package com.pja.bloodcount.utils;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ValidationUtil class is used to validate user password and user email
 * @author baglan
 * @version 1.0
 */
@Service
public class CredentialValidationUtil {

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
    private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+_*?=!.()/|])(?=\\S+$).{8,}$", Pattern.CASE_INSENSITIVE);

    /**
     * Email regex pattern
     * ^                         # start-of-string
     * [A-Z0-9._%+-]+            # match characters and symbols in the list, a-z, 0-9, underscore, dot, percent, plus, hyphen
     * @                         # must contain @ symbol
     * (student\.wum\.edu\.pl    # must contain student.wum.edu.pl domain
     * |wum\.edu\.pl             # OR must contain wum.edu.pl domain
     * |gmail\.com)              # OR must contain gmail.com domain
     * $                         # end-of-string
     */
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@((student\\.wum\\.edu\\.pl)|(wum\\.edu\\.pl)|(gmail\\.com))$", Pattern.CASE_INSENSITIVE);

    public static boolean validatePassword(String password) {
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        return matcher.matches();
    }

    public static boolean validateEmail(String email) {
        Matcher matcher = EMAIL_REGEX.matcher(email);
        return matcher.matches();
    }
}

