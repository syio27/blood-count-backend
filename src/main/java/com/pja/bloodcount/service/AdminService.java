package com.pja.bloodcount.service;

import com.pja.bloodcount.constant.MailMessageConstants;
import com.pja.bloodcount.constant.MailSubjectConstants;
import com.pja.bloodcount.dto.request.InviteUserRequest;
import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.exceptions.EmailValidationException;
import com.pja.bloodcount.exceptions.UserConflictException;
import com.pja.bloodcount.htmlcontent.MailHtmlContent;
import com.pja.bloodcount.mapper.UserMapper;
import com.pja.bloodcount.model.Group;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.repository.UserRepository;
import com.pja.bloodcount.service.contract.NotifierService;
import com.pja.bloodcount.utils.PasswordGeneratorUtil;
import com.pja.bloodcount.utils.CredentialValidationUtil;
import com.pja.bloodcount.validation.GroupValidator;
import com.pja.bloodcount.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final GroupValidator groupValidator;
    private final PasswordEncoder passwordEncoder;
    private final NotifierService notifierService;
    private final UserValidator userValidator;
    @Value("${app.url}")
    private String url;

    public void invite(InviteUserRequest inviteRequest) {
        if (!CredentialValidationUtil.validateEmail(inviteRequest.getEmail())) {
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
                .isActive(true)
                .build();

        group.addUser(user);

        final String profileUrl = url + "/profile";
        notifierService.notifyUser(
                inviteRequest.getEmail(),
                MailSubjectConstants.getInviteSubject(),
                MailHtmlContent.getInviteMessage(MailMessageConstants.getInviteMessage(inviteRequest, generatedPassword, group), profileUrl));

        userRepository.save(user);
        log.info("User {} {} is registered", user.getId(), user.getEmail());
    }

    public UserResponse banUser(UUID id) {
        String subject;
        String message;

        User user = userValidator.validateIfExistsAndGet(id);
        if (user.isActive()) {
            user.setActive(false);
            subject = MailSubjectConstants.getBanSubject();
            message = MailMessageConstants.getBanMessage(user.getEmail());
        } else {
            user.setActive(true);
            subject = MailSubjectConstants.getUnbanSubject();
            message = MailMessageConstants.getUnbanMessage();
        }

        notifierService.notifyUser(user.getEmail(), subject, message);

        User savedUser = userRepository.save(user);
        return UserMapper.mapToResponseDTO(savedUser);
    }
}
