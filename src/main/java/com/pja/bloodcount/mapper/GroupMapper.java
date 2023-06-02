package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.response.GroupResponse;
import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.model.Group;
import com.pja.bloodcount.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupMapper {
    public static GroupResponse mapToResponseDTO(Group group){
        return GroupResponse
                .builder()
                .groupNumber(group.getGroupNumber())
                .build();
    }

    public static List<GroupResponse> mapToResponseListDTO(List<Group> groups){
        return groups.stream()
                .map(group -> GroupResponse
                        .builder()
                        .groupNumber(group.getGroupNumber())
                        .build())
                .toList();
    }
}
