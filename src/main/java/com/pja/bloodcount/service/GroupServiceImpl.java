package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.GroupRequest;
import com.pja.bloodcount.dto.response.GroupResponse;
import com.pja.bloodcount.exceptions.GroupConflictException;
import com.pja.bloodcount.exceptions.UserNotFoundException;
import com.pja.bloodcount.mapper.GroupMapper;
import com.pja.bloodcount.model.Group;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.model.enums.GroupType;
import com.pja.bloodcount.repository.GroupRepository;
import com.pja.bloodcount.repository.UserRepository;
import com.pja.bloodcount.service.contract.GroupService;
import com.pja.bloodcount.validation.GroupValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GroupServiceImpl implements GroupService {

    private final GroupRepository repository;
    private final GroupValidator validator;
    private final UserRepository userRepository;

    @Override
    public GroupResponse getGroupByNumber(String groupNumber) {
        log.info("Group is retrieved {} ", groupNumber);
        return GroupMapper.mapToResponseDTO(validator.validateIfExistsAndGet(groupNumber));
    }

    @Override
    public GroupResponse createGroup(GroupRequest request) {
        Optional<Group> existingGroup = repository.findByGroupNumber(request.getGroupNumber());
        if (existingGroup.isPresent()) {
            throw new GroupConflictException("Group with number " + request.getGroupNumber() + " already exists.");
        }
        Group newGroup = GroupMapper.mapRequestToEntity(request);
        newGroup.addUser(null);
        return GroupMapper.mapToResponseDTO(repository.save(newGroup));
    }

    @Override
    public List<GroupResponse> getAllGroups() {
        return GroupMapper.mapToResponseListDTO(repository.findAll());
    }

    @Override
    public List<GroupResponse> getAllGroupsByType(GroupType type){
        return GroupMapper.mapToResponseListDTO(repository.findByGroupType(type));
    }

    @Transactional
    @Override
    public void clearGroupFromUsers(String groupNumber) {
        validator.validateIfExistsAndGet(groupNumber);
        List<User> usersInGroup = userRepository.findByGroup_GroupNumber(groupNumber);
        Group defaultNoGroup = validator.validateIfExistsAndGet("NO_GR");
        usersInGroup.forEach(user -> user.setGroup(defaultNoGroup));
        userRepository.saveAll(usersInGroup);
    }

    @Override
    public void deleteGroup(String groupNumber) {
        Group group = validator.validateIfExistsAndGet(groupNumber);
        List<User> usersInGroup = userRepository.findByGroup_GroupNumber(groupNumber);
        Group defaultNoGroup = validator.validateIfExistsAndGet("NO_GR");
        usersInGroup.forEach(user -> user.setGroup(defaultNoGroup));
        userRepository.saveAll(usersInGroup);
        repository.delete(group);
    }

    @Override
    public void deleteUserFromGroup(String groupNumber, UUID userId) {
        validator.validateIfExistsAndGet(groupNumber);
        Group defaultNoGroup = validator.validateIfExistsAndGet("NO_GR");
        List<User> usersInGroup = userRepository.findByGroup_GroupNumber(groupNumber);
        User userInGroup = usersInGroup.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(userId));
        userInGroup.setGroup(defaultNoGroup);
        userRepository.save(userInGroup);
    }

    @Override
    public List<GroupResponse> getPublicGroups(){
        List<Group> studentGroups = repository.findByGroupType(GroupType.STUDENT_GROUP);
        studentGroups.removeIf(group -> group.getGroupNumber().equals("NO_GR"));
        return GroupMapper.mapToResponseListDTO(studentGroups);
    }
}
