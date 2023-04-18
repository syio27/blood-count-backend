package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
    User getUserByEmail(String email);
    List<User> getUsers();
    void delete(UUID id);
    void update(UUID id, User user);
    Page<User> getUsers(Pageable pageable);
}
