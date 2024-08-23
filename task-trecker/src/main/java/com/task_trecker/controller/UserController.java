package com.task_trecker.controller;

import com.task_trecker.mapper.user.UserMapper;
import com.task_trecker.model.entitiy.User;
import com.task_trecker.response.UserResponse;
import com.task_trecker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
@Slf4j
public class UserController {

    private final UserService service;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper mapper;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @GetMapping
    public Flux<UserResponse> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("User: {} requested list of all users", userDetails.getUsername());
        return service.findAll()
                .map(mapper::userToResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Mono<ResponseEntity<UserResponse>> getById(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        if (id == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        log.info("User: {} requested user info by id {}", userDetails.getUsername(), id);
        return service.findById(id)
                .map(mapper::userToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PostMapping
    public Mono<ResponseEntity<UserResponse>> createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Created new user {}", user.getUsername());
        return service.save(user)
                .map(mapper::userToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable String id,
                                                         @RequestBody User user,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        if (id == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        log.info("User: {} updated user info by id: {}", userDetails.getUsername(), id);
        return service.update(id, user)
                .map(mapper::userToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        if (id == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        log.info("User: {} deleted user: {}", userDetails.getUsername(), id);
        return service.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }

}
