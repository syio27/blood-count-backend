package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.dto.request.GroupRequest;
import com.pja.bloodcount.dto.response.GroupResponse;

import java.util.List;

public interface GroupService {
    GroupResponse getGroupByNumber(String groupNumber);
    GroupResponse createGroup(GroupRequest request);
    List<GroupResponse> getAllGroups();
}
