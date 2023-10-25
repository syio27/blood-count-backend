package com.pja.bloodcount.service;

import com.pja.bloodcount.constant.MailMessageConstants;
import com.pja.bloodcount.constant.MailSubjectConstants;
import com.pja.bloodcount.dto.request.*;
import com.pja.bloodcount.dto.response.AuthenticationResponse;
import com.pja.bloodcount.dto.response.SimpleGameResponse;
import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.exceptions.*;
import com.pja.bloodcount.htmlcontent.MailHtmlContent;
import com.pja.bloodcount.mapper.GameMapper;
import com.pja.bloodcount.mapper.UserMapper;
import com.pja.bloodcount.model.*;
import com.pja.bloodcount.model.enums.Role;
import com.pja.bloodcount.model.enums.Status;
import com.pja.bloodcount.repository.*;
import com.pja.bloodcount.service.auth.JwtService;
import com.pja.bloodcount.service.contract.NotifierService;
import com.pja.bloodcount.service.contract.UserService;
import com.pja.bloodcount.utils.CredentialValidationUtil;
import com.pja.bloodcount.validation.GroupValidator;
import com.pja.bloodcount.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserAnswerRepository userAnswerRepository;
    private final PatientRepository patientRepository;
    private final TokenRepository tokenRepository;
    private final UserValidator userValidator;
    private final GroupValidator groupValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ResetTokenService tokenService;
    private final NotifierService notifierService;

    @Value("${app.url}")
    private String url;

    @Override
    public UserResponse getUserById(UUID id) {
        log.info("User is retrieved {} ", id);
        return UserMapper.mapToResponseDTO(userValidator.validateIfExistsAndGet(id));
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.info("User is retrieved {} ", email);
        return UserMapper.mapToResponseDTO(userValidator.validateEmailAndGet(email));
    }

    @Override
    public List<UserResponse> getUsersByRole(Role role) {
        List<User> users = repository.findUserByRole(role);
        List<UserResponse> userResponseList = UserMapper.mapToResponseListDTO(users);
        log.info("Users are retrieved {} ->", userResponseList);
        return userResponseList;
    }

    @Override
    public void delete(UUID id) {
        User user = userValidator.validateIfExistsAndGet(id);
        Group group = user.getGroup();
        userAnswerRepository.deleteUserAnswerByUser(user);
        user.getGames().forEach(game -> patientRepository.delete(game.getPatient()));
        group.removeUser(user);
        repository.deleteById(id);
    }

    @Override
    public UserResponse update(UUID id, EmailChangeRequest incomingEmailChangeRequest) {
        User user = userValidator.validateIfExistsAndGet(id);
        User updatedUserDetails = UserMapper.mapToUserModel(incomingEmailChangeRequest, id);

        if (!CredentialValidationUtil.validateEmail(incomingEmailChangeRequest.getEmail())) {
            throw new EmailValidationException("Email is not valid, does not contain @ or .");
        }

        if (repository.findUserByEmail(updatedUserDetails.getEmail()).isPresent()
                && !user.getEmail().equals(updatedUserDetails.getEmail())) {
            throw new UserConflictException(updatedUserDetails.getEmail());
        }

        updatedUserDetails.setRole(getRoleOfUser(user));
        updatedUserDetails.setPassword(getPasswordOfUser(user));

        var savedUser = repository.save(updatedUserDetails);
        return UserMapper.mapToResponseDTO(savedUser);
    }

    @Override
    public Page<UserResponse> getUsers(Pageable pageable) {
        Page<User> entityPage = repository.findAll(pageable);
        List<User> users = entityPage.getContent();
        return new PageImpl<>(UserMapper.mapToResponseListDTO(users), pageable, entityPage.getTotalElements());
    }

    public AuthenticationResponse changePassword(UUID id, PasswordChangeDTO passwordChangeDTO) {
        User user = findById(id);

        if (!CredentialValidationUtil.validatePassword(passwordChangeDTO.getNewPassword())) {
            throw new PasswordValidationException("Password is not valid, doesnt match regex rule");
        }

        if (!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), user.getPassword())) {
            throw new IncorrectOldPasswordException("Old password is incorrect");
        }

        if (passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getOldPassword())) {
            throw new PasswordSameAsOldException("New password cannot be the same as old password");
        }

        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getNewPasswordRepeat())) {
            throw new PasswordRepeatException("New password and new password confirmation are not the same");
        }
        user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        repository.save(user);
        var jwtToken = jwtService.generateToken(user, 0);
        var jwtExpirationDate = jwtService.extractExpiration(jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .expirationDate(jwtExpirationDate)
                .build();
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        if (!CredentialValidationUtil.validateEmail(request.getEmail())) {
            throw new EmailValidationException("Email is not valid, doesnt match regex rule");
        }

        if (repository.findUserByEmail(request.getEmail()).isEmpty()) {
            throw new UserWithEmailNotFoundException(request.getEmail());
        }
        Token token = tokenService.createToken(request.getEmail());

        final String resetUrl = String.format("%s/reset-password/%s?email=%s", url, token.getToken(), request.getEmail());
        final String buttonLabel = "Reset Password";
        notifierService.notifyUser(request.getEmail(), MailSubjectConstants.getForgotPasswordSubject(),
                MailHtmlContent.getHtmlMessage(
                        MailMessageConstants.getForgotPasswordMessage(),
                        resetUrl,
                        buttonLabel,
                        true));
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        Token tokenEntity = tokenRepository.findByToken(request.getToken());

        TokenValidationRequest tokenValidationRequest = TokenValidationRequest
                .builder()
                .token(request.getToken())
                .email(request.getEmail())
                .build();

        if (!tokenService.validateToken(tokenValidationRequest)) {
            throw new ResetTokenInvalidException("Invalid or expired token");
        }

        if (!CredentialValidationUtil.validatePassword(request.getNewPassword())) {
            throw new PasswordValidationException("Password is not valid, doesnt match regex rule");
        }

        if (!request.getNewPassword().equals(request.getNewPasswordRepeat())) {
            throw new PasswordRepeatException("New password and new password confirmation are not the same");
        }

        User user = userValidator.validateEmailAndGet(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedDateTime = now.format(formatter);

        final String loginUrl = String.format("%s/%s", url, "login");
        final String buttonLabel = "Login";
        notifierService.notifyUser(request.getEmail(), MailSubjectConstants.getResetPasswordSubject(),
                MailHtmlContent.getHtmlMessage(
                        MailMessageConstants.getResetPasswordMessage(formattedDateTime),
                        loginUrl,
                        buttonLabel,
                        true));

        tokenRepository.delete(tokenEntity);
    }

    @Transactional
    public void assignUserToGroup(UUID id, UserGroupAssignmentRequest request) {
        User user = findById(id);
        Group group = groupValidator.validateIfExistsAndGet(request.getGroupNumber());
        group.addUser(user);
        log.info("User with email: {} has been successfully added to {} group", user.getEmail(), group.getGroupNumber());
        repository.save(user);
    }

    @Transactional
    public void assignGroupToUsers(UserGroupBatchAssignmentRequest request) {
        Group group = groupValidator.validateIfExistsAndGet(request.getGroupNumber());

        for (UUID userId : request.getUserIds()) {
            User user = findById(userId);

            group.addUser(user);
        }

        repository.saveAll(request.getUserIds().stream()
                .map(userValidator::validateIfExistsAndGet)
                .collect(Collectors.toList()));
    }

    @Override
    public List<UserResponse> getGroupParticipants(String groupNumber) {
        groupValidator.validateIfExistsAndGet(groupNumber);
        return UserMapper.mapToResponseListDTO(repository.findByGroup_GroupNumber(groupNumber));
    }

    @Override
    public SimpleGameResponse getUserGameById(UUID userId, Long gameId) {
        User user = userValidator.validateIfExistsAndGet(userId);
        Optional<Game> optionalGame = user.getGames().stream().filter(g -> Objects.equals(g.getId(), gameId)).findFirst();
        if (optionalGame.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }
        Game usersGame = optionalGame.get();
        if (usersGame.getStatus().equals(Status.IN_PROGRESS)) {
            throw new GameCompleteException("Game %d is still in progress".formatted(usersGame.getId()));
        }
        return GameMapper.mapToSimpleResponseDTO(usersGame);
    }

    private User findById(UUID id) {
        return userValidator.validateIfExistsAndGet(id);
    }

    private Role getRoleOfUser(User user) {
        return user.getRole();
    }

    private String getPasswordOfUser(User user) {
        return user.getPassword();
    }
}
