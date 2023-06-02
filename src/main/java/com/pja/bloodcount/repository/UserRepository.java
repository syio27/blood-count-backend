package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.User;
import com.pja.bloodcount.model.enums.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserById(UUID id);
    Optional<User> findUserByEmail(String email);
    @EntityGraph(attributePaths = {"group"})
    List<User> findUserByRole(Role role);
}
