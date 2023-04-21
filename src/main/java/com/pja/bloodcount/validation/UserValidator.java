package com.pja.bloodcount.validation;

import com.pja.bloodcount.exceptions.UserNotFoundException;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserValidator extends EntityValidator<User, UUID> {

    private final UserRepository repository;

    @Autowired
    public UserValidator(UserRepository repository){
        super(repository);
        this.repository = repository;
    }

    /**
     * Validates if User entity with Email exists, if not throws Runtime Exception (UserNotFoundException)
     * @param email of type String
     * @return User object instance
     */
    public User validateEmailAndGet(String email) {
        return repository.findUserByEmail(email).orElseThrow(() -> getNotFoundException(email));
    }

    @Override
    protected RuntimeException getCollectionIsEmptyException() {
        return null;
    }

    @Override
    protected RuntimeException getNotFoundException(UUID id) {
        return new UserNotFoundException(id);
    }

    protected RuntimeException getNotFoundException(String email) {
        return new UserNotFoundException(email);
    }
}
