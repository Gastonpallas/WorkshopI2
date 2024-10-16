<?php
$client_id = '46462982626617485'; // Remplacez par votre ID d'application
$redirect_uri = 'https://962bd318db454017fb63b07dc7439cc7.serveo.net/Workshop/callback.php'; // URL de redirection
$scope = 'user_profile,user_media';

$auth_url = "https://api.instagram.com/oauth/authorize?client_id={$client_id}&redirect_uri={$redirect_uri}&scope={$scope}&response_type=code";

header("Location: $auth_url");
exit;
