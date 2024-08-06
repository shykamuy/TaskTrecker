package com.task_trecker.mapper.task;

import com.task_trecker.mapper.user.UserMapper;
import com.task_trecker.model.entitiy.Task;
import com.task_trecker.response.TaskResponse;
import com.task_trecker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.stream.Collectors;


public abstract class TaskMapperDelegate implements TaskMapper {

    @Autowired
    private UserService service;

    @Autowired
    private UserMapper mapper;

    @Override
    public TaskResponse taskToResponse(Task task) {
        TaskResponse response = new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getStatus().toString(),
                task.getAuthor(),
                task.getAssignee(),
                task.getObservers()
        );
        return response;
    }
}
