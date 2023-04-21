package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.UserRequest;
import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.mapper.UserMapper;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.repository.UserRepository;
import com.pja.bloodcount.service.contract.UserService;
import com.pja.bloodcount.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.pja.bloodcount.exceptions.ResourceConflictException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserValidator validator;

    @Override
    public UserResponse getUserById(UUID id) {
        log.info("User is retrieved {} ", id);
        return UserMapper.mapToResponseDTO(validator.validateIfExistsAndGet(id));
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.info("User is retrieved {} ", email);
        return UserMapper.mapToResponseDTO(validator.validateEmailAndGet(email));
    }

    @Override
    public List<UserResponse> getUsers() {
        List<UserResponse> userResponseList = UserMapper.mapToResponseListDTO(validator.validateIfAnyExistsAndGet());
        log.info("Users are retrieved {} ->", userResponseList);
        return userResponseList;
    }

    @Override
    public void delete(UUID id) {
        validator.validateIfExistsAndGet(id);
        repository.deleteById(id);
    }

    @Override
    public void update(UUID id, UserRequest incomingUserRequest) {
        User user = validator.validateIfExistsAndGet(id);
        User incomingUser = UserMapper.mapToUserModel(incomingUserRequest, id);
        if(repository.findUserByEmail(incomingUser.getEmail()).isPresent()
                && !user.getEmail().equals(incomingUser.getEmail())){
            throw new ResourceConflictException(incomingUser.getEmail());
        }

        repository.save(incomingUser);
    }

    @Override
    public Page<UserResponse> getUsers(Pageable pageable) {
        Page<User> entityPage = repository.findAll(pageable);
        List<User> users = entityPage.getContent();
        return new PageImpl<>(UserMapper.mapToResponseListDTO(users), pageable, entityPage.getTotalElements());
    }

    /**
     * find user by id or throw RuntimeException
     * @param id of User entity
     * @return User
     */
    private User findById(UUID id){
        return validator.validateIfExistsAndGet(id);
    }
}
