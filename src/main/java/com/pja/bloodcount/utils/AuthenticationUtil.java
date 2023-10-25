package com.pja.bloodcount.utils;

import com.pja.bloodcount.exceptions.UserNotAllowedException;
import com.pja.bloodcount.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AuthenticationUtil {

    public static final String PAYLOAD_MESSAGE = "Access restricted, user with email: %s not allowed to this url";

    private AuthenticationUtil() {
        // private contractor
    }

    public static void isRequestFromSameUser(Authentication authentication, UUID id) {
        User userDetails = (User) authentication.getPrincipal();
        if (!userDetails.getId().equals(id)) {
            throw new UserNotAllowedException(
                    PAYLOAD_MESSAGE.formatted(userDetails.getEmail()));
        }
    }
}
