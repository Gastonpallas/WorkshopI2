package com.example.back;

import com.example.back.Client.Client;
import com.example.back.Client.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Process {

    private final ClientRepository clientRepository;

    @Autowired
    public Process(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void startProcess(){
        System.out.println("test");

        // Récupérer la liste des clients
        List<Client> listeClient = clientRepository.findAll();

        // Pour chaque Client
        listeClient.forEach((client -> {
            // récupérer le token
            // brunoClass(client);

            // Appel à l'API pour récupérer tout les messages -> simulation
            // String instagramMessagesUrl = "https://graph.instagram.com/me/messages?access_token=" + accessToken;

            // Filtre sur la date, si c'est abonnée ou pas

            // Pour chaque message

            // Classer le message grâce à l'IA

            // SI catégorie A = API instagram pour supprimer, voir bloquer, voir masquer etc.... -> simulation

            // Historiser les messages
        }));
    }
}
