<?php
if (isset($_GET['code'])) {
    $code = $_GET['code'];
    
    $url = 'https://api.instagram.com/oauth/access_token';
    $data = [
        'client_id' => '46462982626617485', // ID d'application
        'client_secret' => '9cef7145bd3adb1bc59afd02a01dd7e9', // Remplacez par votre Client Secret
        'grant_type' => 'authorization_code',
        'redirect_uri' => 'https://962bd318db454017fb63b07dc7439cc7.serveo.net/Workshop/callback.php', // URL de redirection
        'code' => $code,
    ];

    $options = [
        'http' => [
            'header'  => "Content-type: application/x-www-form-urlencoded\r\n",
            'method'  => 'POST',
            'content' => http_build_query($data),
        ],
    ];

    $context  = stream_context_create($options);
    $result = file_get_contents($url, false, $context);
    
    if ($result === FALSE) {
        die('Erreur lors de la récupération du token.');
    }

    $response = json_decode($result, true);
    
    if (isset($response['access_token'])) {
        $accessToken = $response['access_token'];
        // Vous pouvez stocker le token dans une base de données ou une session
        echo "Token d'accès : " . htmlspecialchars($accessToken);
    } else {
        echo "Erreur : " . htmlspecialchars($response['error_message']);
    }
} else {
    echo "Aucun code de retour trouvé.";
}
?>
