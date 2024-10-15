package com.example.back;

import com.example.back.Client.Client;
import com.example.back.Client.ClientRepository;
import com.example.back.data.Data;
import com.example.back.data.MessageResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service
public class Process {

    private final ClientRepository clientRepository;

    @Autowired
    public Process(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public static final LocalDateTime DATE_20_MINUTES_AGO = LocalDateTime.now().minusMinutes(2000000000);

    /**
     * Start process
     */
    public void startProcess() {

        // Récupérer la liste des clients
        List<Client> listeClient = clientRepository.findAll();

        // Pour chaque Client
        listeClient.forEach((client -> {
            String token = "token";

            ArrayList<Data> dataList = callAPIInstagram(token);

            dataList.forEach((data) -> {
                // Filtre sur la date, si c'est abonnée ou pas
                try {
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(data.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
                    LocalDate localDate = offsetDateTime.toLocalDate();

                    if (localDate.isAfter(DATE_20_MINUTES_AGO.toLocalDate()) && !data.getRecipient().isFollower()) {

                        double toxicityScore = callAPIPerspective(data.getMessage());

                        // Check toxicity and delete if necessary
                        if (toxicityScore > 0.8) {  // Threshold for toxicity
                            System.out.println("Toxic message detected. Deleting...");
                            deleteMessage(data.getId(), token);  // Delete the message
                        }

                        // Log the message
                        logMessage(data, toxicityScore);
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
        String url = "https://79938d12-de45-49b7-95b9-4d5327d3f5ed.mock.pstmn.io/instagram/messages?token=" + token;

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
        String apiKey = "AIzaSyDcJrVeCLfX6Pu_Tq1GP8-g0ns1JNPtjUw";
        String url = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + apiKey;

        String jsonPayload = String.format(
                "{ \"comment\": { \"text\": \"%s\" }, \"requestedAttributes\": { \"TOXICITY\": {} } }",
                message
        );

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
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
        String url = "https://graph.instagram.com/" + messageId + "?access_token=" + accessToken;

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Message deleted successfully.");
            } else {
                System.err.println("Failed to delete message: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error deleting message", e);
        }
    }

    /**
     * Log the processed message for auditing
     *
     * @param data          the message data
     * @param toxicityScore the toxicity score
     */
    private void logMessage(Data data, double toxicityScore) {
        System.out.println("Logging message: " + data.getMessage() + " | Toxicity score: " + toxicityScore);
        // You can add logic to store this data into a database if needed
    }
}
