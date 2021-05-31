package com.sope.controller.user;

import com.google.common.base.Function;
import com.sope.domain.user.User;
import com.sope.domain.user.UserDTO;

public class UserDTOConverter implements Function<User, UserDTO> {

    @Override
    public UserDTO apply(User user) {
        UserDTO userDto = new UserDTO();
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getUncrypted());
        return userDto;
    }
}
