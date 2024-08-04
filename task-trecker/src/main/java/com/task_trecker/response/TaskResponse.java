package com.task_trecker.response;

import com.task_trecker.model.TaskStatus;
import com.task_trecker.model.entitiy.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;


import java.time.Instant;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
public class TaskResponse {

    private String id;

    private String name;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;

    private String status;

    private User author;

    private User assignee;

    private Set<String> observerIds;

}
