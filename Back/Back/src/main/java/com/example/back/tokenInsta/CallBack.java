package com.example.back.tokenInsta;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class CallBack {

    private final String clientId = "46462982626617485"; // Remplacez par votre ID d'application
    private final String clientSecret = "9cef7145bd3adb1bc59afd02a01dd7e9"; // Remplacez par votre Client Secret
    private final String redirectUri = "https://962bd318db454017fb63b07dc7439cc7.serveo.net/Workshop/callback"; // Remplacez par votre URL de redirection
    private final String accessTokenUrl = "https://api.instagram.com/oauth/access_token";

    private final RestTemplate restTemplate;

    public CallBack(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String exchangeCodeForAccessToken(String code) throws Exception {
        // Préparer les données à envoyer avec la requête POST
        Map<String, String> requestData = new HashMap<>();
        requestData.put("client_id", clientId);
        requestData.put("client_secret", clientSecret);
        requestData.put("grant_type", "authorization_code");
        requestData.put("redirect_uri", redirectUri);
        requestData.put("code", code);

        // Construire la requête HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestData, headers);

        // Envoyer la requête POST pour obtenir le token
        ResponseEntity<Map> response = restTemplate.exchange(
                accessTokenUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        // Vérifier la réponse et obtenir le token
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("access_token")) {
                return (String) responseBody.get("access_token");
            } else {
                throw new Exception("Erreur : " + responseBody.get("error_message"));
            }
        } else {
            throw new Exception("Erreur lors de la récupération du token.");
        }
    }
}
