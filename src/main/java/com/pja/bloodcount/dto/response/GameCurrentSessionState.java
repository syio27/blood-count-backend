package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.enums.Pages;
import com.pja.bloodcount.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameCurrentSessionState {

    private Long gameId;
    private Status status;
    private Pages currentPage;
}