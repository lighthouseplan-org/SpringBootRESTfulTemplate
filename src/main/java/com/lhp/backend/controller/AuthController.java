package com.lhp.backend.controller;

import com.lhp.backend.common.BaseResponse;
import com.lhp.backend.dto.LoginUser;
import com.lhp.backend.dto.UserView;
import com.lhp.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public BaseResponse<UserView> login(@RequestHeader HttpHeaders header, @RequestBody LoginUser user) {
        return authService.login(header, user);
    }

}
