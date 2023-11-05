<?php

include "common.php";

if($_SERVER["REQUEST_METHOD"] != "POST") {
    returnError("Špatná metoda přístupu", 3);
}

// request body
$json = file_get_contents("php://input");
$request = json_decode($json, true);
if(is_null($request)) {
    returnError("Nelze dekódovat JSON", 2);
}

if(!array_key_exists("userId", $request) or !array_key_exists("amount", $request)) {
    returnError("Chybné parametry", 2);
}

$userId = $request["userId"];
$amount = $request["amount"];

// Zjistit, jestli dané userId vůbec existuje
$stmt = $pdo->prepare("SELECT * FROM Users WHERE UserId = ?");
$stmt->execute(array($userId));
if($stmt->rowCount() == 0) {
    returnError("Takový uživatel neexistuje", 1);
}

$stmt = $pdo->prepare("UPDATE Users SET Credit = Credit + ? WHERE UserId = ?");
$stmt->execute(array($amount, $userId));

http_response_code(200);
?>
