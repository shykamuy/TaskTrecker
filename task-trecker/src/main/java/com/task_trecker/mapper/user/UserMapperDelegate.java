package com.task_trecker.mapper.user;

import com.task_trecker.model.entitiy.User;
import com.task_trecker.response.UserResponse;

public abstract class UserMapperDelegate implements UserMapper {

    @Override
    public UserResponse userToResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
