package com.lhp.backend.service;

import com.lhp.backend.common.BaseResponse;
import com.lhp.backend.constant.HeaderKey;
import com.lhp.backend.dto.LoginUser;
import com.lhp.backend.dto.UserView;
import com.lhp.backend.i18n.LocalLanguage;
import com.lhp.backend.constant.StatusCode;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private JwtEncoder jwtEncoder;
    private LocalLanguage messageSource;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(JwtEncoder jwtEncoder,
                       LocalLanguage messageSource,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.jwtEncoder = jwtEncoder;
        this.messageSource = messageSource;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param authentication
     * @return token
     */
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 60 * 60 * 24 * 2L; // 2 days
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public BaseResponse<UserView> login(HttpHeaders header, LoginUser user) {
        String lang = header.toSingleValueMap().get(HeaderKey.localLanguage);
        try {
            String encodePassword = user.getPassword();
            Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),encodePassword));

            String token = generateToken(authentication);
//            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//            authorities.add(new SimpleGrantedAuthority(authentication.getAuthorities().iterator().next().getAuthority()));
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            logger.info("Login success: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            return new BaseResponse<UserView>(String.valueOf(Response.Status.ACCEPTED.getStatusCode()), messageSource.getMessageStr(lang, "success"), new UserView(user.getUsername(), token));
        } catch (DisabledException e) {
            //must be thrown if an account is disabled and the
            return new BaseResponse<UserView>(StatusCode.userDisabled, messageSource.getMessageStr(lang, "user.disable"), null);
        }
        catch(LockedException e){
            //must be thrown if an account is locked and the
            return new BaseResponse<UserView>(StatusCode.userLocked, messageSource.getMessageStr(lang, "user.locked"), null);
        }
        catch(BadCredentialsException e){
            //must be thrown if incorrect credentials are
            return new BaseResponse<UserView>(StatusCode.userUnauthed, messageSource.getMessageStr(lang, "user.username.password.error"), null);
        }
    }

}
