package com.sope.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthenticationFailed extends RuntimeException {
    public AuthenticationFailed(String username) {
        super("Authentication failed" + username);
    }
}