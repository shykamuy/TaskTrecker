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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    private final UserService userService;
    private final TaskMapper mapper;
    private final TaskSmallMapper taskSmallMapper;

    @GetMapping
    public Flux<TaskResponse> getAllTasks() {
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
    public Mono<ResponseEntity<TaskResponse>> getById(@PathVariable String id) {
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
    public Mono<ResponseEntity<TaskResponseSmall>> createTask(@RequestBody Task task) {
        return service.save(task)
                .map(taskSmallMapper::taskToSmallResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskResponseSmall>> updateTask(@PathVariable String id, @RequestBody Task task) {
        return service.update(id, task)
                .map(taskSmallMapper::taskToSmallResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return service.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
