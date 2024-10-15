package com.example.back.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Data {
    public String id;
    public User sender;
    public User recipient;
    public String message;
    public String timestamp;
    public boolean is_read ;
    public ArrayList<Attachment> attachments;
}

