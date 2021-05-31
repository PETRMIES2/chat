package com.sope.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONFLICT, reason="Username not available")  // 409
public class UsernameAlreadyTaken extends RuntimeException {

}
