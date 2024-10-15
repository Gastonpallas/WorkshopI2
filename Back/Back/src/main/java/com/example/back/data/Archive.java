package com.example.back.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Archive {
    @Id
    private String id ;
    private String idSender ;
    private String idRecipient ;
    private String message;
    private double score;
    private LocalDate date;

}
