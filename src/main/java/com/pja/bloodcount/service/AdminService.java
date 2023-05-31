package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.InviteUserRequest;
import com.pja.bloodcount.dto.request.RegisterRequest;
import com.pja.bloodcount.dto.response.AuthenticationResponse;
import com.pja.bloodcount.exceptions.EmailValidationException;
import com.pja.bloodcount.exceptions.PasswordValidationException;
import com.pja.bloodcount.exceptions.ResourceConflictException;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.model.enums.Role;
import com.pja.bloodcount.repository.UserRepository;
import com.pja.bloodcount.service.auth.JwtService;
import com.pja.bloodcount.utils.PasswordGeneratorUtil;
import com.pja.bloodcount.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;


    public void invite(InviteUserRequest inviteRequest) {
        if (!ValidationUtil.validateEmail(inviteRequest.getEmail())) {
            throw new EmailValidationException("Email is not valid, doesnt match regex rule");
        }

        if (userRepository.findUserByEmail(inviteRequest.getEmail()).isPresent()) {
            throw new ResourceConflictException(inviteRequest.getEmail());
        }

        String generatedPassword = PasswordGeneratorUtil.generateRandomPassword();

        log.info("User's password generated {}", generatedPassword);

        User user = User.builder()
                .name("Baglan")
                .email(inviteRequest.getEmail())
                .password(passwordEncoder.encode(generatedPassword))
                .role(inviteRequest.getRole())
                .build();

        final String profileUrl = "http://localhost:4200/profile";
        final String subject = "Invitation letter";
        final String message = "Dear " + user.getName() + ",\n\n"
                + "You have been invited to join our application as a supervisor.\n\n"
                + "Here are your login credentials:\n"
                + "Email: " + inviteRequest.getEmail() + "\n"
                + "Temporary password: " + generatedPassword + "\n\n"
                + "Profile page URL: " + profileUrl + "\n\n"
                + "Please log in using these details. We strongly recommend that you change your password immediately after your first login for security purposes.\n\n"
                + "If you have any issues, please feel free to contact us.\n\n"
                + "Best regards,\n"
                + "The Application Team";

        mailService.sendMail(inviteRequest.getEmail(), subject, message);

        userRepository.save(user);
        log.info("User {} {} is registered", user.getId(), user.getEmail());
    }
}
