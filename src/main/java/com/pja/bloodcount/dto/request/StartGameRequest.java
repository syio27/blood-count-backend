package com.pja.bloodcount.dto.request;

import com.pja.bloodcount.model.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartGameRequest {
    private Long caseId;
    private UUID userId;
    private Language language;
}
