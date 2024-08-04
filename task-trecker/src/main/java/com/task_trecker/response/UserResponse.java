package com.task_trecker.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UserResponse {

    private String id;

    private String username;

    private String email;

}
