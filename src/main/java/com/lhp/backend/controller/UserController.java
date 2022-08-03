package com.lhp.backend.controller;

import com.lhp.backend.common.BaseResponse;
import com.lhp.backend.dto.ChangePasswordDto;
import com.lhp.backend.dto.ResetPasswordDto;
import com.lhp.backend.dto.SignupUser;
import com.lhp.backend.dto.UpdatePasswordDto;
import com.lhp.backend.model.User;
import com.lhp.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

  private final UserService service;

  @Autowired
  public UserController(UserService service) {
    this.service = service;
  }


  @PostMapping("/signup")
    public BaseResponse<User> signup(@RequestHeader HttpHeaders header, @RequestBody SignupUser user) {
        return service.signup(header, user);
    }

  // 获取单个用户信息
  @GetMapping("/user/detail")
    public BaseResponse<User> getUser(@RequestHeader HttpHeaders header){
      return service.getUser(header);
    }
  
  // 更新用户信息
  @PostMapping("/user/update")
    public BaseResponse<User> updateProfile(@RequestHeader HttpHeaders header, @RequestBody User user){
      return service.updateProfile(header, user);
    }
  
  /**
   * 已登录用户修改密码
   * @param header 请求头
   * @param changePasswordDto 密码重设信息
   * @return 响应
   */
  @PostMapping("/change-password")
    public BaseResponse<User> changePassword(@RequestHeader HttpHeaders header, @RequestBody ChangePasswordDto changePasswordDto){
      return service.changePassword(header, changePasswordDto);
    }

  /**
   * 重置密码：发送邮件
   * @param header 请求头
   * @param resetPasswordDto 邮箱
   *
   * @return 响应
   */
  @PostMapping("/reset-password")
    public BaseResponse<String> resetPassword(@RequestHeader HttpHeaders header, @RequestBody ResetPasswordDto resetPasswordDto){
      return service.resetPassword(header, resetPasswordDto.getEmail());
    }

  /**
   * 重置密码：跳转页面
   * @param header 请求头
   * @param token 重置密码token
   *
   * @return 响应
   */
    @GetMapping("/reset-password/{token}")
      public BaseResponse<String> resetPasswordPage(@RequestHeader HttpHeaders header, @PathVariable String token){
        return service.resetPasswordValidateToken(header, token);
      }

  /**
   * 重置密码：修改密码
   * @param header 请求头
   * @param updatePasswordDto 重置密码信息
   * @return 响应
   */
    @PostMapping("/reset-password/update-password")
      public BaseResponse<String> resetPasswordUpdate(@RequestHeader HttpHeaders header, @RequestBody UpdatePasswordDto updatePasswordDto){
        return service.resetPasswordUpdate(header, updatePasswordDto);
      }



}
