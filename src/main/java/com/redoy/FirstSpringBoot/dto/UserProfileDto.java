package com.redoy.FirstSpringBoot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long userAccountId;
    private String fullName;
    private String email;
    private String phone;
    private String profilePhoto;
    private boolean status;
    private String role;
    private LocalDateTime createdAt;
}
