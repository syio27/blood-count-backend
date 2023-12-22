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

    @Value("${app.url}")
    private static String url;
    private static final String PROFILE_URL = url + "/profile";
    private static final String BUTTON_LABEL = "Login";

    private final UserRepository userRepository;
    private final GroupValidator groupValidator;
    private final PasswordEncoder passwordEncoder;
    private final NotifierService notifierService;
    private final UserValidator userValidator;

    public void invite(InviteUserRequest inviteRequest) {
        if (!CredentialValidationUtil.validateEmail(inviteRequest.getEmail())) {
            throw new EmailValidationException("Email is not valid, doesnt match regex rule");
        }

        if (userRepository.findUserByEmail(inviteRequest.getEmail()).isPresent()) {
            throw new UserConflictException(inviteRequest.getEmail());
        }

        Group group = groupValidator.validateIfExistsAndGet(inviteRequest.getGroupNumber());

        String generatedPassword = PasswordGeneratorUtil.generateRandomPassword();

        User user = User.builder()
                .email(inviteRequest.getEmail())
                .password(passwordEncoder.encode(generatedPassword))
                .role(inviteRequest.getRole())
                .isActive(true)
                .build();

        group.addUser(user);

        notifierService.notifyUser(
                inviteRequest.getEmail(),
                MailSubjectConstants.getInviteSubject(),
                MailHtmlContent.getHtmlMessage(MailMessageConstants.getInviteMessage(inviteRequest, generatedPassword, group), PROFILE_URL, BUTTON_LABEL, true));

        userRepository.save(user);
        log.info("User {} {} is registered", user.getId(), user.getEmail());
    }

    public UserResponse toggleBanUser(UUID id) {
        String subject;
        String message;

        User user = userValidator.validateIfExistsAndGet(id);
        if (user.isActive()) {
            user.setActive(false);
            subject = MailSubjectConstants.getBanSubject();
            message = MailHtmlContent.getHtmlMessage(MailMessageConstants.getBanMessage(user.getEmail()), null, null, false);
        } else {
            user.setActive(true);
            subject = MailSubjectConstants.getUnbanSubject();
            message = MailHtmlContent.getHtmlMessage(MailMessageConstants.getUnbanMessage(), PROFILE_URL, BUTTON_LABEL, true);
        }

        notifierService.notifyUser(user.getEmail(), subject, message);

        User savedUser = userRepository.save(user);
        return UserMapper.mapToResponseDTO(savedUser);
    }
}
