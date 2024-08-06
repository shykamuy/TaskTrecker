package com.task_trecker.service;

import com.task_trecker.model.TaskStatus;
import com.task_trecker.model.entitiy.Task;
import com.task_trecker.model.entitiy.User;
import com.task_trecker.repository.TaskRepository;
import com.task_trecker.repository.UserRepository;
import com.task_trecker.response.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;

    private final UserService userService;
    private final UserRepository userRepository;

    public Flux<Task> findAll() {
        return repository.findAll();
    }

    public Mono<Task> findById(String id) {
        return repository.findById(id);
    }

    public Mono<Task> save(Task task) {
        task.setId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
        return repository.save(task);
    }

    public Mono<Task> update(String id, Task task) {
        return findById(id).flatMap(taskForUpdate -> {
            if (StringUtils.hasText(task.getName())) {
                taskForUpdate.setName(task.getName());
            }
            if (StringUtils.hasText(task.getDescription())) {
                taskForUpdate.setDescription(task.getDescription());
            }
            if (task.getStatus() != null) {
                taskForUpdate.setStatus(task.getStatus());
            }
            if (StringUtils.hasText(task.getAuthorId())) {
                taskForUpdate.setAuthorId(task.getAuthorId());
            }
            if (StringUtils.hasText(task.getAssigneeId())) {
                taskForUpdate.setAssigneeId(task.getAssigneeId());
            }
            if (!task.getObserverIds().isEmpty()) {
                taskForUpdate.setObserverIds(task.getObserverIds());
            }
            taskForUpdate.setUpdatedAt(Instant.now());
            return repository.save(taskForUpdate);
        });
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

}
