package com.lhp.backend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class SignupUser {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String phone;
}
