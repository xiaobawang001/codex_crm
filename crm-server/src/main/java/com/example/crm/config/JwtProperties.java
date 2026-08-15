package com.example.crm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "crm.jwt")
public class JwtProperties {

    private String secret;
    private long expireHours = 24;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpireHours() {
        return expireHours;
    }

    public void setExpireHours(long expireHours) {
        this.expireHours = expireHours;
    }
}
