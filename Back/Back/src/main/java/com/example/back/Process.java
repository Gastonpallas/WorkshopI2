package com.example.back;

import com.example.back.Client.Client;
import com.example.back.Client.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
            String token = "token";

            // Appel à l'API pour récupérer tout les messages -> simulation
            // String instagramMessagesUrl = "https://graph.instagram.com/me/messages?access_token=" + accessToken;
            String url = "https://a34085de-49ac-4e44-905d-f7907e1849d1.mock.pstmn.io/instagram/messages?token=" + token;

            // Créer le client HTTP
            HttpClient httpClient = HttpClient.newHttpClient();

            // Créer la requête GET
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // Envoyer la requête et recevoir la réponse
            HttpResponse<String> response = null;
            try {
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Afficher la réponse
            System.out.println(response.body());

            // Filtre sur la date, si c'est abonnée ou pas

            // Pour chaque message

            // Classer le message grâce à l'IA

            // SI catégorie A = API instagram pour supprimer, voir bloquer, voir masquer etc.... -> simulation

            // Historiser les messages
        }));
    }
}
