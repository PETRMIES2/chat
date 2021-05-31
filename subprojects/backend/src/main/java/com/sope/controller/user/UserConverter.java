package com.sope.controller.user;

import com.google.common.base.Function;
import com.sope.domain.user.User;
import com.sope.domain.user.UserDTO;

public class UserConverter implements Function<UserDTO, User> {

    @Override
    public User apply(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        user.setPassword(userDTO.getPassword());
        return user;
    }
}
