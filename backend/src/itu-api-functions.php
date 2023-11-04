<?php
/*
Autor: Robin Volf (xvolfr00)
*/

// Funkce pro autentizaci uživatele pomocí emailu a hesla
// Heslo se porovnává s uloženým hešem
function authenticateUser($email, $password) {
    global $pdo;
    $response = array();

    $stmt = $pdo->prepare("SELECT PasswordHash FROM Users WHERE Email = ?");

    $stmt->execute(array($email));
    if($stmt->rowCount() == 0) { // Taková kombinace neexistuje
        $response[STATUS_KEY] = STATUS_VAL_ERR;
        $response[ERR_MSG_KEY] = "Uživatel s mailem $email neexistuje";
    } else {
        if(password_verify($password, $stmt->fetchColumn()) == true) {
            $response[STATUS_KEY] = STATUS_VAL_OK;
        } else {
            $response[STATUS_KEY] = STATUS_VAL_ERR;
            $response[ERR_MSG_KEY] = "Nesprávné heslo";
        }
    }
    echo json_encode($response);
}

?>
