package com.pja.bloodcount.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeDTO {
    private String oldPassword;
    private String newPassword;
    private String newPasswordRepeat;
}
