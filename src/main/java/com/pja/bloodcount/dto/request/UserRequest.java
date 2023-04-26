package com.pja.bloodcount.dto.request;

import com.pja.bloodcount.model.enums.Role;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.Email;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @Column(nullable = false)
    private String name;
    @Email
    @Column(unique = true, nullable = false)
    private String email;
}
