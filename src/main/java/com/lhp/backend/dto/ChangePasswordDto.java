package com.lhp.backend.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private String email;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
