package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.PasswordChangeDTO;
import com.pja.bloodcount.dto.request.EmailChangeRequest;
import com.pja.bloodcount.dto.request.UserGroupAssignmentRequest;
import com.pja.bloodcount.dto.request.UserGroupBatchAssignmentRequest;
import com.pja.bloodcount.dto.response.AuthenticationResponse;
import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.exceptions.*;
import com.pja.bloodcount.mapper.UserMapper;
import com.pja.bloodcount.model.Group;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.model.enums.Role;
import com.pja.bloodcount.repository.UserRepository;
import com.pja.bloodcount.service.auth.JwtService;
import com.pja.bloodcount.service.contract.UserService;
import com.pja.bloodcount.utils.ValidationUtil;
import com.pja.bloodcount.validation.GroupValidator;
import com.pja.bloodcount.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserValidator userValidator;
    private final GroupValidator groupValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
        userValidator.validateIfExistsAndGet(id);
        repository.deleteById(id);
    }

    @Override
    public UserResponse update(UUID id, EmailChangeRequest incomingEmailChangeRequest) {
        User user = userValidator.validateIfExistsAndGet(id);
        User updatedUserDetails = UserMapper.mapToUserModel(incomingEmailChangeRequest, id);

        if(!ValidationUtil.validateEmail(incomingEmailChangeRequest.getEmail())){
            throw new EmailValidationException("Email is not valid, does not contain @ or .");
        }

        if(repository.findUserByEmail(updatedUserDetails.getEmail()).isPresent()
                && !user.getEmail().equals(updatedUserDetails.getEmail())){
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

    public AuthenticationResponse changePassword(UUID id, PasswordChangeDTO passwordChangeDTO){
        User user = findById(id);

        if(!ValidationUtil.validatePassword(passwordChangeDTO.getNewPassword())){
            throw new PasswordValidationException("Password is not valid, doesnt match regex rule");
        }

        if(!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), user.getPassword())){
            throw new IncorrectOldPasswordException("Old password is incorrect");
        }

        if(passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getOldPassword())){
            throw new PasswordSameAsOldException("New password cannot be the same as old password");
        }

        if(!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getNewPasswordRepeat())){
            throw new PasswordRepeatException("New password and new password confirmation are not the same");
        }
        repository.save(user);
        user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        var jwtToken = jwtService.generateToken(user, 0);
        var jwtExpirationDate = jwtService.extractExpiration(jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .expirationDate(jwtExpirationDate)
                .build();
    }

    @Transactional
    public void assignUserToGroup(UUID id, UserGroupAssignmentRequest request){
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
    public List<UserResponse> getGroupParticipants(String groupNumber){
        groupValidator.validateIfExistsAndGet(groupNumber);
        return UserMapper.mapToResponseListDTO(repository.findByGroup_GroupNumber(groupNumber));
    }

    /**
     * find user by id or throw RuntimeException
     * @param id of User entity
     * @return User
     */
    private User findById(UUID id){
        return userValidator.validateIfExistsAndGet(id);
    }

    private Role getRoleOfUser(User user){
        return user.getRole();
    }

    private String getPasswordOfUser(User user){
        return user.getPassword();
    }
}
