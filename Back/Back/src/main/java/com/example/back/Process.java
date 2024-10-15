package com.example.back;

import com.example.back.Client.Client;
import com.example.back.Client.ClientRepository;
import com.example.back.data.Data;
import com.example.back.data.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

    public static final LocalDateTime DATE_20_MINUTES_AGO = LocalDateTime.now().minusMinutes(2000000000);

    public void startProcess(){

        // Récupérer la liste des clients
        List<Client> listeClient = clientRepository.findAll();

        // Pour chaque Client
        listeClient.forEach((client -> {
            // récupérer le token
            // brunoClass(client);
            String token = "token";

            ArrayList<Data> dataList = callAPIInstagram(token);

            dataList.forEach((data) -> {
                // Pour chaque message
                // Filtre sur la date, si c'est abonnée ou pas
                try {
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(data.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
                    LocalDate localDate = offsetDateTime.toLocalDate();

                    if(localDate.isAfter( DATE_20_MINUTES_AGO.toLocalDate()) && !data.getRecipient().isFollower()){

                        // Classer le message grâce à l'IA
                        callAPIPerspective(data);

                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Erreur lors de la conversion: " + e.getMessage());
                }
            });
        }));
    }

    private ArrayList<Data> callAPIInstagram(String token) {
        // Appel à l'API pour récupérer tout les messages -> simulation
        String url = "https://79938d12-de45-49b7-95b9-4d5327d3f5ed.mock.pstmn.io/instagram/messages?token=" + token;

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

        System.out.println(response.body());
        // Convertir la réponse JSON en objets Java (POJOs)

        ObjectMapper mapper = new ObjectMapper();
        MessageResponse messageResponse;
        try {
            messageResponse = mapper.readValue(response.body(), MessageResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return messageResponse.getData();
    }

    private void callAPIPerspective(Data data) {
        // SI catégorie A = API instagram pour supprimer, voir bloquer, voir masquer etc.... -> simulation

        System.out.println(data.getMessage());
        // Historiser les messages
    }

}
