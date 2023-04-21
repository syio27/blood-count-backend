package com.pja.bloodcount.service;

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
    public User getUserById(UUID id) {
        log.info("User is retrieved {} ", id);
        return validator.validateIfExistsAndGet(id);
    }

    @Override
    public User getUserByEmail(String email) {
        log.info("User is retrieved {} ", email);
        return validator.validateEmailAndGet(email);
    }

    @Override
    public List<User> getUsers() {
        List<User> users = repository.findAll();
        log.info("Users are retrieved {} ->", users);
        return users;
    }

    @Override
    public void delete(UUID id) {
        validator.validateIfExistsAndGet(id);
        repository.deleteById(id);
    }

    @Override
    public void update(UUID id, User incommingUser) {
        User user = validator.validateIfExistsAndGet(id);

        if(repository.findUserByEmail(incommingUser.getEmail()).isPresent()
                && !user.getEmail().equals(incommingUser.getEmail())){
            throw new ResourceConflictException(incommingUser.getEmail());
        }

        repository.save(incommingUser);
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        Page<User> entityPage = repository.findAll(pageable);
        List<User> users = entityPage.getContent();
        return new PageImpl<>(users, pageable, entityPage.getTotalElements());
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
