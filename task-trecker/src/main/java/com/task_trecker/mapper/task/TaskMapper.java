package com.task_trecker.mapper.task;

import com.task_trecker.model.entitiy.Task;
import com.task_trecker.response.TaskResponse;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
@DecoratedWith(TaskMapperDelegate.class)
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    TaskResponse taskToResponse(Task task);

}
