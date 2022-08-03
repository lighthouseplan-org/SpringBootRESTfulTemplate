package com.lhp.backend.i18n;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "i18n")
public class I18NMessage {
    /**
     * message-key:<lang:message>
     */
    // private Map<String, Map<String, String>> messages;
    /**
     * Default language setting (Default "cn").
     */
    private String defaultLang = "jp";

    /**
     * get i18n message
     *
     * @param key
     * @param language
     * @return
     */
    // public String message(String key, String language) {
    // return Optional.ofNullable(message.get(
    // language))
    // .map(map -> map.get(key))
    // .orElse(key);
    // }

    /**
     * get i18n message
     *
     * @param key
     * @param language
     * @return
     */
    // public String message(String key, String language) {
    // return Optional.ofNullable(messages.get(
    // language))
    // .map(map -> map.get(key))
    // .orElse(key);
    // }
}
