package com.task_trecker.controller;

import com.task_trecker.mapper.user.UserMapper;
import com.task_trecker.model.entitiy.User;
import com.task_trecker.response.UserResponse;
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

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    private final UserMapper mapper;

    @GetMapping
    public Flux<UserResponse> getAllUsers() {
        return service.findAll()
                .map(mapper::userToResponse);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getById(@PathVariable String id) {
        return service.findById(id)
                .map(mapper::userToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PostMapping
    public Mono<ResponseEntity<UserResponse>> createUser(@RequestBody User user) {
        return service.save(user)
                .map(mapper::userToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable String id, @RequestBody User user) {
        return service.update(id, user)
                .map(mapper::userToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return service.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }

}
