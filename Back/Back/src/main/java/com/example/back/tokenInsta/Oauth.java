package com.example.back.tokenInsta;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Oauth {

    public Oauth(RestTemplate restTemplate, CallBack callBack) {
        this.restTemplate = restTemplate;
        this.callBack = callBack;
    }
    private final String clientId = "46462982626617485"; // Remplacez par votre ID d'application
    private final String redirectUri = "https://962bd318db454017fb63b07dc7439cc7.serveo.net/Workshop/callback"; // URL de redirection
    private final String scope = "user_profile,user_media"; // Scopes requis
    private final String authUrl = "https://api.instagram.com/oauth/authorize";

    private final RestTemplate restTemplate;
    private final CallBack callBack;



    public String getToken(String mail, String pasword) throws Exception {


        // GET code
        String fullAuthUrl = authUrl + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=" + scope
                + "&response_type=code";

        // Effectuer un appel GET vers l'URL OAuth (note : dans ce cas, il n'y aura pas de redirection pour l'utilisateur)
        String code = restTemplate.getForObject(fullAuthUrl, String.class).toString() ;


        // POST get token with code
        String token = callBack.exchangeCodeForAccessToken(code);

        return token;
    }

}
