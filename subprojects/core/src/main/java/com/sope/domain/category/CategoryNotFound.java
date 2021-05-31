package com.sope.domain.category;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="No such category;)")  // 400
public class CategoryNotFound extends RuntimeException {

    public CategoryNotFound(final String category) {
        super(category);
    }
}
