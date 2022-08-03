package com.lhp.backend.service;


import com.lhp.backend.i18n.LocalLanguage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Logger logger = LogManager.getLogger(EmailService.class);
    private final LocalLanguage messageSource;

    public EmailService(LocalLanguage messageSource) {
        this.messageSource = messageSource;
    }


    public void send(String email, String subject, String body) {
        //TODO: send email
        logger.info("send email to {} with subject {} and body {}", email, subject, body);
    }

    public void sendResetPasswordEmail(String email, String token, String lang) {
        //TODO
        String appUrl = "http://localhost:9090/reset-password/" + token;
        String subject = messageSource.getMessageStr(lang,"reset.your.password");
        String body = messageSource.getMessageStr(lang,"please.click.reset") + appUrl;
        this.send(email, subject, body);
    }
}
