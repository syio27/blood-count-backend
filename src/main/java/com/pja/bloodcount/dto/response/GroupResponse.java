package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.enums.GroupType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
    private String groupNumber;
    private GroupType groupType;
}
