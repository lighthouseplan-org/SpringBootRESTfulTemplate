package com.lhp.backend.service;

import com.lhp.backend.model.User;
import com.lhp.backend.model.UserToken;
import com.lhp.backend.repository.UserTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;

    public UserTokenService(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    public String generatePasswordResetToken() {
        // generate token by using UUID
        return java.util.UUID.randomUUID().toString();
    }

    @Transactional
    public String generateAndSavePasswordResetToken(User user) {
        String token = generatePasswordResetToken();
        // save token to user
        try{
            // if user token is exist, delete it
            userTokenRepository.deleteByUserId(user.getId());
            userTokenRepository.save(new UserToken(token, user.getId()));
        }catch(Exception e){
            throw e;
        }
        return token;
    }

    @Transactional
    public boolean isValidToken(String token) {
        UserToken userToken = userTokenRepository.findByToken(token);
        if (userToken == null) {
            return false;
        }
        if (userToken.getExpiryDate().getTime() < System.currentTimeMillis()) {
            userTokenRepository.deleteById(userToken.getId());
            return false;
        }
        return true;
    }
}
