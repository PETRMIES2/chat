package com.sope.domain.chat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such topic")  // 404
public class ChatTopicNotFound extends RuntimeException {

}
