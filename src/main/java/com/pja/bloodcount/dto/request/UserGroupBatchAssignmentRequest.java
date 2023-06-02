package com.pja.bloodcount.dto.request;

import com.pja.bloodcount.model.enums.GroupType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupBatchAssignmentRequest {

    private List<UUID> userIds;
    private String groupNumber;
}