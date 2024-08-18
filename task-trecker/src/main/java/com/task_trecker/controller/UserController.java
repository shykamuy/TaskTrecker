package com.task_trecker.controller;

import com.task_trecker.mapper.user.UserMapper;
import com.task_trecker.model.entitiy.User;
import com.task_trecker.response.UserResponse;
import com.task_trecker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;

    private final UserMapper mapper;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @GetMapping
    public Flux<UserResponse> getAllUsers() {
        return service.findAll()
                .map(mapper::userToResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Mono<ResponseEntity<UserResponse>> getById(@PathVariable String id) {
        if (id == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        return service.findById(id)
                .map(mapper::userToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PostMapping
    public Mono<ResponseEntity<UserResponse>> createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return service.save(user)
                .map(mapper::userToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable String id, @RequestBody User user) {
        if (id == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        return service.update(id, user)
                .map(mapper::userToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        if (id == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        return service.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }

}
