package com.task_trecker.mapper.tasksmall;

import com.task_trecker.mapper.task.TaskMapperDelegate;
import com.task_trecker.model.entitiy.Task;
import com.task_trecker.response.TaskResponseSmall;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@DecoratedWith(TaskSmallMapperDelegate.class)
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskSmallMapper {

    TaskResponseSmall taskToSmallResponse(Task task);
}
