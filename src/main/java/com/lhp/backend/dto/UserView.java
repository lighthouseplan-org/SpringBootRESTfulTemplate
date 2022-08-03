package com.lhp.backend.dto;


import lombok.Data;

@Data
public class UserView {
    private String username;
    private String token;

    public UserView(String username, String token) {
        this.username = username;
        this.token = token;
    }
}
