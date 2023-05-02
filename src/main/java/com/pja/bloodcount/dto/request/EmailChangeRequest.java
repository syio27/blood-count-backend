package com.pja.bloodcount.dto.request;

import lombok.*;

import javax.persistence.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailChangeRequest {
    @Column(unique = true, nullable = false)
    private String email;
}
