package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.Group;
import com.pja.bloodcount.model.enums.GroupType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

    Optional<Group> findByGroupNumber(String groupNumber);
    List<Group> findByGroupType(GroupType type);
}
