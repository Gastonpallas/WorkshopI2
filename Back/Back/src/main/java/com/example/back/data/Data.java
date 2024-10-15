package com.example.back.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Data {
    private String id;
    private User sender;
    private User recipient;
    private String message;
    private String timestamp;
    private boolean read ;
    private ArrayList<Attachment> attachments;
}

