package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.GroupRequest;
import com.pja.bloodcount.dto.response.GroupResponse;
import com.pja.bloodcount.exceptions.GroupConflictException;
import com.pja.bloodcount.mapper.GroupMapper;
import com.pja.bloodcount.model.Group;
import com.pja.bloodcount.repository.GroupRepository;
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
                .build();
        return GroupMapper.mapToResponseDTO(repository.save(newGroup));
    }

    @Override
    public List<GroupResponse> getAllGroups() {
        return GroupMapper.mapToResponseListDTO(repository.findAll());
    }
}
