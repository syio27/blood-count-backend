package com.pja.bloodcount.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class PasswordGeneratorUtil {

    private static final int PASSWORD_LENGTH = 12;
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

    public static String generateRandomPassword() {
        return RandomStringUtils.random(PASSWORD_LENGTH, characters);
    }
}
