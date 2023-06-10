package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.InviteUserRequest;
import com.pja.bloodcount.exceptions.EmailValidationException;
import com.pja.bloodcount.exceptions.UserConflictException;
import com.pja.bloodcount.model.Group;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.repository.GroupRepository;
import com.pja.bloodcount.repository.UserRepository;
import com.pja.bloodcount.utils.PasswordGeneratorUtil;
import com.pja.bloodcount.utils.ValidationUtil;
import com.pja.bloodcount.validation.GroupValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupValidator groupValidator;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public void invite(InviteUserRequest inviteRequest) {
        if (!ValidationUtil.validateEmail(inviteRequest.getEmail())) {
            throw new EmailValidationException("Email is not valid, doesnt match regex rule");
        }

        if (userRepository.findUserByEmail(inviteRequest.getEmail()).isPresent()) {
            throw new UserConflictException(inviteRequest.getEmail());
        }

        Group group = groupValidator.validateIfExistsAndGet(inviteRequest.getGroupNumber());

        String generatedPassword = PasswordGeneratorUtil.generateRandomPassword();

        log.info("User's password generated {}", generatedPassword);

        User user = User.builder()
                .email(inviteRequest.getEmail())
                .password(passwordEncoder.encode(generatedPassword))
                .role(inviteRequest.getRole())
                .build();

        group.addUser(user);

        final String profileUrl = "http://localhost:4200/profile";
        final String subject = "Invitation letter";
        final String message = "Dear new User\n\n"
                + "You have been invited to join our application as a " + inviteRequest.getRole().toString().toLowerCase() + ".\n\n"
                + "Here are your login credentials:\n"
                + "Email: " + inviteRequest.getEmail() + "\n"
                + "Temporary password: " + generatedPassword + "\n"
                + "Assigned Group: " + group.getGroupNumber() + "\n"
                + "Group Type: " + group.getGroupType() + "\n\n"
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
