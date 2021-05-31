package com.sope.domain.category;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="No such show")  // 400
public class TvShowNotFound extends RuntimeException {
    public TvShowNotFound(final String name) {
        super(name);
    }

}
