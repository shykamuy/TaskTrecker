package com.task_trecker.mapper.user;

import com.task_trecker.mapper.task.TaskMapperDelegate;
import com.task_trecker.model.entitiy.User;
import com.task_trecker.response.UserResponse;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@DecoratedWith(UserMapperDelegate.class)
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponse userToResponse(User user);

}
