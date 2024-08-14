package com.task_trecker.repository;

import com.task_trecker.model.entitiy.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Optional<User> findByUsername(String username);


}
