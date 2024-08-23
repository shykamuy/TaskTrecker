package com.task_trecker.controller;

import com.task_trecker.mapper.task.TaskMapper;
import com.task_trecker.mapper.tasksmall.TaskSmallMapper;
import com.task_trecker.mapper.user.UserMapper;
import com.task_trecker.model.entitiy.Task;
import com.task_trecker.response.TaskResponse;
import com.task_trecker.response.TaskResponseSmall;
import com.task_trecker.service.TaskService;
import com.task_trecker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService service;

    private final UserService userService;
    private final TaskMapper mapper;
    private final TaskSmallMapper taskSmallMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Flux<TaskResponse> getAllTasks(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("User: {} requested list of all tasks", userDetails.getUsername());
        return service.findAll()
                .flatMap(task -> Mono.just(task)
                        .zipWith(userService.findById(task.getAuthorId()))
                        .zipWith(userService.findById(task.getAssigneeId()))
                        .zipWith(Flux.fromIterable(task.getObserverIds()).flatMap(userService::findById).collectList())
                        .map(tuple -> {
                            TaskResponse response = mapper.taskToResponse(tuple.getT1().getT1().getT1());
                            response.setAuthor(tuple.getT1().getT1().getT2());
                            response.setAssignee(tuple.getT1().getT2());
                            response.setObservers(new HashSet<>(tuple.getT2()));
                            return response;
                        }));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Mono<ResponseEntity<TaskResponse>> getById(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        if (id == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        log.info("User: {} requested task by ID: {}", userDetails.getUsername(), id);

        var monoTask = service.findById(id)
                .zipWhen(task -> userService.findById(task.getAuthorId()))
                .zipWhen(tuple -> userService.findById(tuple.getT1().getAssigneeId()))
                .zipWhen(tuple2 -> {
                    List<String> observerIds = new ArrayList<>(tuple2.getT1().getT1().getObserverIds());
                    return Flux.fromIterable(observerIds)
                            .flatMap(userService::findById)
                            .collectList();
                })
                .map(tuple -> {
                    TaskResponse task = mapper.taskToResponse(tuple.getT1().getT1().getT1());
                    task.setAuthor(tuple.getT1().getT1().getT2());
                    task.setAssignee(tuple.getT1().getT2());
                    task.setObservers(new HashSet<>(tuple.getT2()));
                    return task;
                });

        return monoTask
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public Mono<ResponseEntity<TaskResponseSmall>> createTask(@RequestBody Task task, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("User {} created new task: {}", userDetails.getUsername(), task.getDescription());
        return service.save(task)
                .map(taskSmallMapper::taskToSmallResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/observer")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Mono<ResponseEntity<TaskResponseSmall>> addObserver(@RequestParam String taskId,
                                                               @RequestParam String observerId,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        if (taskId == null || observerId == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        log.info("User: {} added observer: {} for task: {}", userDetails.getUsername(), observerId, taskId);

        return userService.findById(observerId)
                .zipWhen(tuple -> service.findById(taskId))
                .zipWhen(tuple -> {
                    var observers = tuple.getT2().getObservers();
                    var observerIds = tuple.getT2().getObserverIds();

                    observers.add(tuple.getT1());
                    observerIds.add(observerId);

                    tuple.getT2().setObservers(observers);
                    tuple.getT2().setObserverIds(observerIds);
                    return service.update(taskId, tuple.getT2());
                })
                .map(tuple -> {
                    TaskResponseSmall task = taskSmallMapper.taskToSmallResponse(tuple.getT2());
                    return task;
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public Mono<ResponseEntity<TaskResponseSmall>> updateTask(@PathVariable String id,
                                                              @RequestBody Task task,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        if (id == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        log.info("User: {} updated task: {}", userDetails.getUsername(), id);

        return service.update(id, task)
                .map(taskSmallMapper::taskToSmallResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        if (id == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        log.info("User: {} deleted task: {}", userDetails.getUsername(), id);

        return service.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
