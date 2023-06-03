package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.GroupRequest;
import com.pja.bloodcount.dto.response.GroupResponse;
import com.pja.bloodcount.exceptions.GroupConflictException;
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
        Group newGroup = Group
                .builder()
                .groupNumber(request.getGroupNumber())
                .groupType(request.getGroupType())
                .build();

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
        log.info("Group: {} has been successfully cleared", groupNumber);
        userRepository.saveAll(usersInGroup);
    }
}
