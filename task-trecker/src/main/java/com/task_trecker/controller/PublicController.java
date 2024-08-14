package com.task_trecker.controller;

import com.task_trecker.model.entitiy.User;
import com.task_trecker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {

    private final UserService userService;

    @PostMapping("/account")
    public Mono<ResponseEntity<User>> createUserAccount(@RequestBody User user) {
        return Mono.just(ResponseEntity.status(HttpStatus.CREATED)
                .body(createAccount(user)));
    }
    private User createAccount(User user) {

        var createdUser = userService.save(user);

        return user;
    }

}
