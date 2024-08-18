package com.task_trecker.service;

import com.task_trecker.model.entitiy.User;
import com.task_trecker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public Flux<User> findAll() {
        return repository.findAll();
    }

    public Mono<User> findById(String id) {
        return repository.findById(id);
    }

    public Mono<User> findByUsername(String username) {
        return repository.findByUsername(username);
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
