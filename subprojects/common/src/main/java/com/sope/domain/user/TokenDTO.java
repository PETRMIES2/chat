package com.sope.domain.user;

public class TokenDTO {
    private String token;

    public TokenDTO(String key) {
        token = key;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
