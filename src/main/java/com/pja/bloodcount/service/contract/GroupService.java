package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.dto.request.GroupRequest;
import com.pja.bloodcount.dto.response.GroupResponse;
import com.pja.bloodcount.model.enums.GroupType;

import java.util.List;

public interface GroupService {
    GroupResponse getGroupByNumber(String groupNumber);
    GroupResponse createGroup(GroupRequest request);
    List<GroupResponse> getAllGroups();
    List<GroupResponse> getAllGroupsByType(GroupType type);
    void clearGroupFromUsers(String groupNumber);
}
