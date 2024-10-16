package com.example.back;

import com.example.back.Client.Client;
import com.example.back.Client.ClientRepository;
import com.example.back.data.Archive;
import com.example.back.data.ArchiveRepository;
import com.example.back.data.Data;
import com.example.back.data.MessageResponse;
import com.example.back.tokenInsta.Oauth;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.example.back.ApiUrls.*;

@Service
public class Process {

    private final ClientRepository clientRepository;
    private final ArchiveRepository archiveRepository;
    private final Oauth oauth;


    @Autowired
    public Process(ClientRepository clientRepository, ArchiveRepository archiveRepository, Oauth oauth) {
        this.clientRepository = clientRepository;
        this.archiveRepository = archiveRepository;
        this.oauth = oauth;
    }



    public static final LocalDateTime DATE_20_MINUTES_AGO = LocalDateTime.now().minusMinutes(2000000000);
    public static final double ACCEPTED_TOXICITY_SCORE = 0.2;

    /**
     * Start process
     */
    public void startProcess(){

        // Récupérer la liste des clients
        List<Client> listeClient = clientRepository.findAll();
        System.out.println("[INFO] Found " + listeClient.size() + " clients");


        // Pour chaque Client
        listeClient.forEach((client -> {
            String token = "test";
            try {
                token = oauth.getToken(client.getMail(), client.getPassword());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            ArrayList<Data> dataList = callAPIInstagram(token);

            String finalToken = token;
            dataList.forEach((data) -> {
                // Filtre sur la date, si c'est abonnée ou pas
                try {
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(data.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
                    LocalDate localDate = offsetDateTime.toLocalDate();

                    if(localDate.isAfter( DATE_20_MINUTES_AGO.toLocalDate()) && !data.getRecipient().isFollower()){

                        System.out.println("Get toxicity score from Perspective API");

                        double toxicityScore = callAPIPerspective(data.getMessage());

                        // Check toxicity and delete if necessary
                        if (ACCEPTED_TOXICITY_SCORE < toxicityScore) {  // Threshold for toxicity
                            System.out.println("Toxic message detected with score: " + toxicityScore + ". Deleting message ID: " + data.getId());;
                            deleteMessage(data.getId(), finalToken);  // Delete the message
                            archiveMessage(data, toxicityScore, localDate);

                        }
                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Erreur lors de la conversion: " + e.getMessage());
                }
            });
        }));
    }

    /**
     * Call Instagram API to get the list of messages
     *
     * @param token necessary to log to the API
     * @return List of messages
     */
    private ArrayList<Data> callAPIInstagram(String token) {
        String url = API_GET_MESSAGE + "/instagram/messages?token=" + token;

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Get messages from instagram API");
        System.out.println(response.body());

        ObjectMapper mapper = new ObjectMapper();
        MessageResponse messageResponse;
        try {
            messageResponse = mapper.readValue(response.body(), MessageResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return messageResponse.getData();
    }

    /**
     * Call Perspective API to get the toxicity score
     *
     * @param message message categorized
     * @return score
     */
    private double callAPIPerspective(String message) {
        String jsonPayload = String.format(
                "{ \"comment\": { \"text\": \"%s\" }, \"requestedAttributes\": { \"TOXICITY\": {} } }",
                message
        );

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_PERSPECTIVE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(response.body());

            return responseJson
                    .path("attributeScores")
                    .path("TOXICITY")
                    .path("summaryScore")
                    .path("value")
                    .asDouble(0.0);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error calling Perspective API", e);
        }
    }

    /**
     * Delete message on Instagram
     *
     * @param messageId   the message ID to delete
     * @param accessToken the token to authenticate the request
     */
    private void deleteMessage(String messageId, String accessToken) {
        String url = API_DELETE_MESSAGE + "?access_token=" + accessToken + "?id_message" + messageId;

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {

                System.out.println("Delete message from instagram API");
                System.out.println("Message deleted successfully.");
            } else {
                System.err.println("Failed to delete message: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error deleting message", e);
        }
    }


    /**
     * Archive Message in DB
     *
     * @param data          data to archive
     * @param toxicityScore is the score from perspective API
     * @param dateMessage is the date when the message was sent
     */

    void archiveMessage(Data data, double toxicityScore, LocalDate dateMessage) {
        Archive archive = new Archive();
        archive.setId(data.getId()); // Using message ID as archive ID
        archive.setIdSender(data.getSender().getId()); // Assuming User class has getId method
        archive.setIdRecipient(data.getRecipient().getId());
        archive.setMessage(data.getMessage());
        archive.setScore(toxicityScore); // Get the toxicity score
        archive.setDate(dateMessage); // Archive the current date

        archiveRepository.save(archive); // Save the archive to the database
        System.out.println("Message archived: " + data.getMessage());
    }


}

