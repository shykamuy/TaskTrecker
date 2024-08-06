package com.task_trecker.response;

import com.task_trecker.model.entitiy.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
public class TaskResponseSmall {

    private String id;

    private String name;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;

    private String status;

    private String authorId;

    private String assigneeId;

    private Set<String> observerIds;
}
