package com.lhp.backend.service;

import com.lhp.backend.common.BaseResponse;
import com.lhp.backend.constant.HeaderKey;
import com.lhp.backend.constant.RoleType;
import com.lhp.backend.dto.ChangePasswordDto;
import com.lhp.backend.dto.SignupUser;
import com.lhp.backend.dto.UpdatePasswordDto;
import com.lhp.backend.i18n.LocalLanguage;
import com.lhp.backend.model.User;
import com.lhp.backend.model.UserToken;
import com.lhp.backend.repository.UserRepository;
import com.lhp.backend.repository.UserTokenRepository;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private LocalLanguage messageSource;
    private PasswordEncoder passwordEncoder;

    private final Logger logger = org.apache.logging.log4j.LogManager.getLogger(UserService.class);

    private final EmailService emailService;
    private final UserTokenRepository userTokenRepository;
    private final UserTokenService userTokenService;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, LocalLanguage messageSource, EmailService emailService, UserTokenRepository userTokenRepository, UserTokenService userTokenService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.emailService = emailService;
        this.userTokenRepository = userTokenRepository;
        this.userTokenService = userTokenService;
    }


    public BaseResponse<User> demo(@RequestHeader HttpHeaders header) {
        BaseResponse<User> ret = new BaseResponse<>();
        String retCodeStr = String.valueOf(Response.Status.OK.getStatusCode());
        ret.setCode(retCodeStr.toString());
        String lang = header.toSingleValueMap().get(HeaderKey.localLanguage);

        String hello = messageSource.getMessageStr(lang, "hello");
        ret.setMessage(hello);
        return ret;
    }

    public BaseResponse<User> signup(HttpHeaders header, SignupUser user) {
        BaseResponse<User> ret = new BaseResponse<>();
        String lang = header.toSingleValueMap().get(HeaderKey.localLanguage);

        // check if user exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            ret.setCode(String.valueOf(Response.Status.CONFLICT.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.name.exists"));
        } else if (userRepository.findByEmail(user.getEmail()) != null) {
            ret.setCode(String.valueOf(Response.Status.CONFLICT.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.email.exists"));
        } else if (! user.getPassword().equals(user.getConfirmPassword())) {
            ret.setCode(String.valueOf(Response.Status.CONFLICT.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "password.not.match"));
        } 
        else {
            User u = createUser(user);
            userRepository.save(u);
            ret.setCode(String.valueOf(Response.Status.CREATED.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.created")); // development only
        }

        return ret;
    }
    // 获取单个用户信息
    public BaseResponse<User> getUser(HttpHeaders header){
        BaseResponse<User> ret = new BaseResponse<>();
        String lang = header.toSingleValueMap().get(HeaderKey.localLanguage);
        String id = getMyId();
        User u;
        try{
            u = userRepository.findById(id).get();
            ret.setCode(String.valueOf(Response.Status.FOUND.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.found"));
            ret.setData(u);
        }catch (Exception e){
            ret.setCode(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.unfound"));
        }
        return ret;
    }
    // 更新用户信息
    public BaseResponse<User> updateProfile(HttpHeaders header, User user){
        BaseResponse<User> ret = new BaseResponse<>();
        String lang = header.toSingleValueMap().get(HeaderKey.localLanguage);
        String id = getMyId();
        User dbData = userRepository.findById(id).get();
        if(id!=null && dbData != null){
            if(user.getUsername()!=null){
                userRepository.setUsername(user.getUsername(), dbData.getId());
            }
            if(user.getPhone()!=null){
                userRepository.setPhone(user.getPhone(), dbData.getId());
            }
            if(user.getEmail()!=null){
                userRepository.setEmail(user.getEmail(), dbData.getId());
            }
            ret.setData(userRepository.findById(id).get());
            ret.setCode(String.valueOf(Response.Status.RESET_CONTENT.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.reseted"));
            return ret;
        }
        ret.setCode(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()));
        ret.setMessage(messageSource.getMessageStr(lang, "user.unfound"));

        return ret;
    }

    private User createUser(SignupUser user) {
        User u = new User();
        u.setUsername(user.getUsername());
        u.setPassword(passwordEncoder.encode(user.getPassword()));
        u.setPhone(user.getPhone());
        u.setEmail(user.getEmail());
        u.setRoleId(RoleType.USER.getRoleId()); // by default, user is a role_user
        u.setUserId(user.getUsername() + System.currentTimeMillis()); // by default, user id is username + current time
        return u;
    }

    public String getMyId(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String userId = userRepository.findByUsername(username).get().getId();
        logger.info("userId: " + userId);
        return userId;
    }

    public String getMyName(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("username: " + username);
        return username;
    }

    public BaseResponse<User> changePassword(HttpHeaders header, ChangePasswordDto changePasswordDto) {
        // todo: test
        BaseResponse<User> ret = new BaseResponse<>();
        String lang = header.toSingleValueMap().get(HeaderKey.localLanguage);
        String oldPassword = changePasswordDto.getOldPassword();
        // get current user password
        if (checkIfValidOldPassword(oldPassword)) {
            String newPassword = changePasswordDto.getNewPassword();
            if(newPassword.equals(changePasswordDto.getConfirmPassword())){
                userRepository.updatePassword(passwordEncoder.encode(newPassword), getMyId());
                ret.setCode(String.valueOf(Response.Status.OK.getStatusCode()));
                ret.setMessage(messageSource.getMessageStr(lang, "user.reseted"));
            }else{
                ret.setCode(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()));
                ret.setMessage(messageSource.getMessageStr(lang, "user.password.confirm.error"));
            }
        }else {
            ret.setCode(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.password.error"));
        }
        return ret;
    }

    private boolean checkIfValidOldPassword(String oldPassword) {
        return passwordEncoder.matches(oldPassword, userRepository.findById(getMyId()).get().getPassword());
    }

    public BaseResponse<String> resetPassword(HttpHeaders header, String email) {
        BaseResponse<String> ret = new BaseResponse<>();
        String lang = header.toSingleValueMap().get(HeaderKey.localLanguage);

        User u = userRepository.findByEmail(email);
        if(u != null){
            try {
                generateAndSendPasswordResetToken(u, lang);
            } catch (Exception e){
                ret.setCode(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
                ret.setMessage(messageSource.getMessageStr(lang, "user.reset.error"));
                return ret;
            }
        }else{
            ret.setCode(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.unfound"));
            return ret;
        }
        ret.setCode(String.valueOf(Response.Status.OK.getStatusCode()));
        ret.setMessage(messageSource.getMessageStr(lang, "user.email.sent"));
        return ret;
    }

    private void generateAndSendPasswordResetToken(User u, String lang) {
        String token;
        try{
            token = userTokenService.generateAndSavePasswordResetToken(u);
        } catch (Exception e){
            logger.error("error: " + e.getMessage());
            throw new RuntimeException(e);
        }
        try{
            emailService.sendResetPasswordEmail(u.getEmail(), token, lang);
        } catch (Exception e){
            logger.error("error: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public BaseResponse<String> resetPasswordValidateToken(HttpHeaders header, String token) {
        BaseResponse<String> ret = new BaseResponse<>();
        String lang = header.toSingleValueMap().get(HeaderKey.localLanguage);
        if (userTokenService.isValidToken(token)){
            ret.setCode(String.valueOf(Response.Status.OK.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.token.success"));
        }else{
            ret.setCode(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.token.error"));
        }
        return ret;
    }

    @Transactional
    public BaseResponse<String> resetPasswordUpdate(HttpHeaders header, UpdatePasswordDto updatePasswordDto) {
        BaseResponse<String> ret = new BaseResponse<>();
        String lang = header.toSingleValueMap().get(HeaderKey.localLanguage);
        String token = updatePasswordDto.getToken();
        UserToken userToken = userTokenRepository.findByToken(token);
        if (userToken == null){
            ret.setCode(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.token.error"));
            return ret;
        }

        String userId = userToken.getUserId();
        Optional<User> u = userRepository.findById(userId);
        if(!u.isPresent()) {
            ret.setCode(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.unfound"));
            return ret;
        }
        if (!updatePasswordDto.getNewPassword().equals(updatePasswordDto.getConfirmPassword())){
            ret.setCode(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.password.confirm.error"));
            return ret;
        }
        try {
            userRepository.updatePassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()), userId);
            userTokenRepository.deleteByUserId(userId);
            ret.setCode(String.valueOf(Response.Status.OK.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.reseted"));
        } catch (Exception e){
            logger.error("update error: " + e.getMessage());
            ret.setCode(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
            ret.setMessage(messageSource.getMessageStr(lang, "user.reset.error"));
        }
        return ret;
    }

}
