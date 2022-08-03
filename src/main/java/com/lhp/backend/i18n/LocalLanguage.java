package com.lhp.backend.i18n;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Data
@Configuration
@Component
@EnableAutoConfiguration
// @AutoConfiguration
public class LocalLanguage {

    private static final Logger logger = LogManager.getLogger(LocalLanguage.class);

    // @Value("${languages.defaultLang}")
    // private String defaultLang;

    // @Value("${languages.jp}")
    // private Map<String, String> jpMessage;

    // @Value("${languages.messages}")
    // private Map<String, I18NMessage> messages;

    // @Value("${languages.zh}")
    // private Map<String, String> zhMessage;
    // @Autowired
    // private I18NMessage i18n;

    // @Autowired
    // static LocalLanguage singleton;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NMessage i18nMessage;

    public String getMessageStr(String messageCode) {
        return messageSource.getMessage(messageCode, null, Locale.getDefault());
    }

    public String getMessageStr(String messageCode, Object[] args) {
        return messageSource.getMessage(messageCode, args, Locale.getDefault());
    }

    public String getMessageStr(String messageCode, Object[] args, Locale locale) {
        return messageSource.getMessage(messageCode, args, locale);
    }

    public String getMessageStr(String languageStr, String messageCode) {
        Locale locale;
        String defaultLang = i18nMessage.getDefaultLang();
        if (languageStr == null || languageStr.length() == 0) {
            locale = new Locale(defaultLang);
        } else {
            String[] langArr = languageStr.split("_");
            if (langArr != null) {
                if (langArr.length > 1) {
                    locale = new Locale(langArr[0], langArr[1]);
                } else {
                    locale = new Locale(langArr[0], "");
                }
            } else {
                locale = new Locale(defaultLang);
            }
        }

        return messageSource.getMessage(messageCode, null, locale);
    }

    // public static Map<String, String> getMesage(String lang) {

    // if (singleton == null) {
    // singleton = new LocalLanguage();
    // }

    // Locale locale = new Locale("jp", "JP");
    // String a = singleton.messageSource.getMessage("hello", null, locale);
    // if (lang == null) {
    // lang = singleton.getI18n().getDefaultLang();
    // }
    // Map<String, String> messageMap = singleton.getI18n().getMessages().get(lang);
    // if (messageMap == null) {
    // messageMap = (Map<String, String>) singleton.getI18n().getMessages()
    // .get(singleton.getI18n().getDefaultLang());
    // }
    // logger.info("defaultLang" + singleton.getI18n().getDefaultLang());
    // logger.info("messageMap" + messageMap.toString());
    // return messageMap;
    // }

}
