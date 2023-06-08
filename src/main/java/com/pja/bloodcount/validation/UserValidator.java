package com.pja.bloodcount.validation;

import com.pja.bloodcount.exceptions.UserNotFoundException;
import com.pja.bloodcount.exceptions.UserWithEmailNotFoundException;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Component
public class UserValidator extends EntityValidator<User, UUID> {

    private final UserRepository repository;

    @Autowired
    public UserValidator(UserRepository repository){
        super(repository);
        this.repository = repository;
    }

    /**
     * Validates if User entity with Email exists, if not throws Runtime Exception (UserWithEmailNotFoundException)
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
        return new UserWithEmailNotFoundException(email);
    }
}
