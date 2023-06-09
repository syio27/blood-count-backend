package com.pja.bloodcount.validation;

import com.pja.bloodcount.exceptions.GroupNotFoundException;
import com.pja.bloodcount.model.Group;
import com.pja.bloodcount.repository.GroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class GroupValidator extends EntityValidator<Group, String> {

    private final GroupRepository groupRepository;
    public GroupValidator(JpaRepository<Group, String> repository,
                          GroupRepository groupRepository) {
        super(repository);
        this.groupRepository = groupRepository;
    }

    @Override
    public Group validateIfExistsAndGet(String id){
        return groupRepository.findByGroupNumber(id).orElseThrow(() -> getNotFoundException(id));
    }

    @Override
    protected RuntimeException getCollectionIsEmptyException() {
        return null;
    }

    @Override
    protected RuntimeException getNotFoundException(String groupNumber) {
        return new GroupNotFoundException(groupNumber);
    }
}
