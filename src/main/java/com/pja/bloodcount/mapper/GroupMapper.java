package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.request.GroupRequest;
import com.pja.bloodcount.dto.response.GroupResponse;
import com.pja.bloodcount.model.Group;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupMapper {
    public static GroupResponse mapToResponseDTO(Group group){
        return GroupResponse
                .builder()
                .groupNumber(group.getGroupNumber())
                .groupType(group.getGroupType())
                .totalParticipants(group.getUsers().size())
                .build();
    }

    public static List<GroupResponse> mapToResponseListDTO(List<Group> groups){
        return groups.stream()
                .map(group -> GroupResponse
                        .builder()
                        .groupNumber(group.getGroupNumber())
                        .groupType(group.getGroupType())
                        .totalParticipants(group.getUsers().size())
                        .build())
                .toList();
    }

    public static Group mapRequestToEntity(GroupRequest request) {
        return Group
                .builder()
                .groupNumber(request.getGroupNumber())
                .groupType(request.getGroupType())
                .build();
    }
}
