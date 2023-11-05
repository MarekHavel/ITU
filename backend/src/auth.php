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

if(!array_key_exists("email", $request) or !array_key_exists("password", $request)) {
    returnError("Chybí parametry", 2);
}

$email = $request["email"];
$password = $request["password"];

$stmt = $pdo->prepare("SELECT PasswordHash, UserId FROM Users WHERE Email = ?");

$stmt->execute(array($email));
$row = $stmt->fetch(PDO::FETCH_ASSOC);
if($row === false) {
    returnError("Takový uživatel neexistuje", 1);
} else {
    if(!password_verify($password, $row["PasswordHash"])) {
        returnError("Špatné heslo", 1);
    }
}

$response = array(
    "userId" => $row["UserId"]
);
echo json_encode($response, JSON_UNESCAPED_UNICODE);
?>
