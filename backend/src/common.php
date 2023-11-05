<?php
/*
Autor: Robin Volf (xvolfr00)
*/

header("content-type: application/json; charset=utf-8");
ini_set("default_charset", "UTF-8");
ini_set("output_encoding", "UTF-8");
ini_set("internal_encoding", "UTF-8");

const STATUS_KEY = "responseStatus";
const STATUS_VAL_OK = "ok";
const STATUS_VAL_ERR = "error";
const ERR_MSG_KEY = "errorMessage";
const REQ_TYPE_KEY = "requestType";

include "secrets.php"; // DB_NAME a DB_PASSWORD

function returnError($msg, $code) {
    http_response_code(400);
    $response = array();
    $response["code"] = $code;
    $response["message"] = $msg;
    
    echo json_encode($response, JSON_UNESCAPED_UNICODE);
    exit();
}

try {
    $pdo = new PDO("mysql:host=localhost;dbname=".DB_NAME.";charset=utf8mb4;port=/var/run/mysql/mysql.sock", DB_NAME, DB_PASSWORD);
} catch (PDOException $e) {
    returnError("Nelze se připojit k databázi", 0);
}

?>
