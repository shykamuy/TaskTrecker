package com.task_trecker.securtity;

import com.task_trecker.model.entitiy.User;
import com.task_trecker.repository.UserRepository;
import com.task_trecker.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.task_trecker.model.RoleType.ROLE_USER;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.security", name = "type", havingValue = "db")
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserService userService;


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.fromCallable(() -> userService.findByUsername(username))
                .flatMap(Mono::just)
                .map(AppUserPrincipal::new);
    }

}
