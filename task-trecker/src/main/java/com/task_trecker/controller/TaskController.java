package com.task_trecker.controller;

import com.task_trecker.mapper.task.TaskMapper;
import com.task_trecker.mapper.user.UserMapper;
import com.task_trecker.model.entitiy.Task;
import com.task_trecker.response.TaskResponse;
import com.task_trecker.service.TaskService;
import com.task_trecker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    private final UserService userService;
    private final TaskMapper mapper;

    @GetMapping
    public Flux<TaskResponse> getAllTasks() {
        return service.findAll().map(mapper::taskToResponse);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> getById(@PathVariable String id) {
        var monoTask = service.findById(id)
                .zipWhen(task -> {return userService.findById(task.getAuthorId());}, Tuples::of)
                .zipWhen(tuple1 -> {return userService.findById(tuple1.getT1().getAssigneeId());}, (tuple1, tuple2) -> {
                    var task = mapper.taskToResponse(tuple1.getT1());
                    task.setAuthor(tuple1.getT2());
                    task.setAssignee(tuple2);
                    return task;
                });

        return monoTask
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<TaskResponse>> createTask(@RequestBody Task task) {
        return service.save(task)
                .map(mapper::taskToResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> updateTask(@PathVariable String id, @RequestBody Task task) {
        return service.update(id, task)
                .map(mapper::taskToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return service.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
