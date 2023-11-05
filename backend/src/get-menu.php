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

if(!array_key_exists("userId", $request)) {
    returnError("Chybí parametr userId", 2);
}

$userId = $request["userId"];

if(array_key_exists("date", $request)) {
    $date = $request["date"];
} else {
    $date = date("Y-m-d");
}

$stmt = $pdo->prepare("SELECT CanteenId FROM Users WHERE UserId = ?");
$stmt->execute(array($userId));

if($stmt->rowCount() == 0) {
    returnError("Nesprávné UserId", 1);
}

$canteenId = $stmt->fetchColumn();
// var_dump($canteenId);

$stmt = $pdo->prepare("SELECT DishId FROM Menus WHERE CanteenId = ? AND Date = ?");
$stmt->execute(array($canteenId, $date));

// 0 = DishId
$dishIdArray = $stmt->fetchAll(PDO::FETCH_COLUMN, 0);  
// var_dump($dishIdArray);

$pdo->setAttribute(PDO::ATTR_ORACLE_NULLS, PDO::NULL_TO_STRING); // Reprezentace NULL prázdným řetězcem
$response = array();
foreach($dishIdArray as $dishId){
    // detaily jídla
    $stmt = $pdo->prepare("SELECT * FROM Dishes WHERE DishId = ?");
    $stmt->execute(array($dishId));
    $dish_row = $stmt->fetch(PDO::FETCH_ASSOC);

    // jméno DishCategory
    $stmt = $pdo->prepare("SELECT Name FROM DishCategories WHERE DishCategoryId = ?");
    $stmt->execute(array($dish_row["DishCategoryId"]));
    $category_name = $stmt->fetch(PDO::FETCH_COLUMN, 0);
    
    $dish = array(
        "id" => (int)$dishId,
        "name" => $dish_row["Name"],
        "category" => $category_name,
        "allergens" => $dish_row["Allergens"],
        "itemsLeft" => 100, //TODO
        "weight" => (int)$dish_row["Mass"],
    );
    array_push($response, $dish);
}

echo json_encode($response, JSON_UNESCAPED_UNICODE);
?>
