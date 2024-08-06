package com.task_trecker.mapper.tasksmall;

import com.task_trecker.model.entitiy.Task;
import com.task_trecker.response.TaskResponseSmall;

public abstract class TaskSmallMapperDelegate implements TaskSmallMapper{

    @Override
    public TaskResponseSmall taskToSmallResponse(Task task) {
        TaskResponseSmall smallResponse = new TaskResponseSmall(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getStatus().toString(),
                task.getAuthorId(),
                task.getAssigneeId(),
                task.getObserverIds()
        );
        return smallResponse;
    }
}
