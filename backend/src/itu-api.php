<?php

const STATUS_KEY = "responseStatus";
const STATUS_VAL_OK = "ok";
const STATUS_VAL_ERR = "error";
const ERR_MSG_KEY = "errorMessage";
const REQ_TYPE_KEY = "requestType";

// Citlivé info - v budoucnu ze secret file
$db_name = "name";
$db_pass = "password";

/*
Popis API

Myšlenka byla, že klient dotáže metodou POST s typem dat application/x-www-form-urlencoded
a ve formě klíč=hodnota se budou předávat jednotlivé informace pro dotaz.
Jako odpověď se pak pošle JSON kódovaná odpověď s předem definovanými položkami.

Běžné operace:
    - Autentizace uživatele
    - Získat jídla pro daný den v dané menze
    - Získat detail jídla
    - Ohodnotit jídlo
    - Objednat jídlo
    - Získat kredit uživatele
    - Získat objednávky uživatele ten den
    - Získat všechny objednávky uživatele

"Admin" operace:
    - Přidat menzu(?)
    - Přidat jídlo
    - Přidat nabídku menzy
    - Přidat uživatele
*/

/*
Definice API

Dotaz:
    requestType=*type*
    ... (další nutná políčka)

Odpověď:
    responseStatus= ("ok" nebo "error")
    errorMessage= (pokud je "error")
    ... (klíče s odpověďmi, pokud ok)

Autentizace uživatele
    Klient pošle:
        requestType=authenticateUser
        email=VALUE
        password=VALUE

    Server odpoví:
        responseStatus=ok (netuším jak se bude pak probíhat autorizace - takže se teď vrací jen OK)
        NEBO
        responseStatus=error
        errorMessage=MSG

TODO - další operace
*/

try {
    $pdo = new PDO("mysql:host=localhost;dbname=$db_name;port=/var/run/mysql/mysql.sock", "$db_name", "$db_pass");
} catch (PDOException $e) {
    $response = array(
        STATUS_KEY => STATUS_VAL_ERR,
        ERR_MSG_KEY => "Nelze přistoupit k databázi"
    );
    echo json_encode($response);
}

include "itu-api-functions.php";

if (isset($_POST[REQ_TYPE_KEY])) {
    switch($_POST[REQ_TYPE_KEY]) {
        case "authenticateUser":
            if(isset($_POST["email"]) && isset($_POST["password"])) {
                authenticateUser($_POST["email"], $_POST["password"]);
                return;
            }
        break;
    }
}

$response = array(
    STATUS_KEY => STATUS_VAL_ERR,
    ERR_MSG_KEY => "Neznámý dotaz"
);
echo json_encode($response);

?>
