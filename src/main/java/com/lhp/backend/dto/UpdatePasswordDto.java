package com.lhp.backend.dto;

import lombok.Data;


@Data
public class UpdatePasswordDto {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
