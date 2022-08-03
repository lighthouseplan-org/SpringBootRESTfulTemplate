package com.lhp.backend.service;

import com.lhp.backend.model.User;
import com.lhp.backend.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class MyUserDetailsService implements UserDetailsService {

    
    private final UserRepository userRepository;
    private final Logger logger = LogManager.getLogger(MyUserDetailsService.class);

    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) {
        User u = userRepository
                .findByUsername(username)
                .orElseThrow(
                () -> new UsernameNotFoundException(
                        format("User: %s, not found", username)
                )
        );

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(u.getPassword())
                .roles(u.getRoleId())
                .build();
    }

}
