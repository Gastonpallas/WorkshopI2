package com.example.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/process")
public class ProcessController {

    Process process = new Process();

    @PostMapping
    public void process() {
        System.out.println("Process started");
        process.startProcess();
        // Logique de traitement à insérer ici
    }
}
