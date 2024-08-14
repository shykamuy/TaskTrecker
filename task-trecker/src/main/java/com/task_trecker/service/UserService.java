package com.task_trecker.service;

import com.task_trecker.model.entitiy.User;
import com.task_trecker.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.task_trecker.model.RoleType.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    public Flux<User> findAll() {
        return repository.findAll();
    }

    public Mono<User> findById(String id) {
        return repository.findById(id);
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username not found!"));
    }

    public Mono<User> save(User user) {
        user.setId(UUID.randomUUID().toString());
        System.out.println(user);
        return repository.save(user);
    }

    public Mono<User> update(String id, User user) {
        return findById(id).flatMap(userForUpdate -> {
            if (StringUtils.hasText(user.getUsername())) {
                userForUpdate.setUsername(user.getUsername());
            }
            if (StringUtils.hasText(user.getEmail())) {
                userForUpdate.setEmail(user.getEmail());
            }

            return repository.save(userForUpdate);
        });
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }



}
