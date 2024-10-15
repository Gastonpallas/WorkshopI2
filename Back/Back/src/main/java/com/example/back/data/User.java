package com.example.back.data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private String id;
    private String username;
    private boolean follower;
}
