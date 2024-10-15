package com.example.back.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class MessageResponse {
    public ArrayList<Data> data;
    public Object paging;
}
