package com.example.back.data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    public String id;
    public String username;
    public boolean isFollower;
}
