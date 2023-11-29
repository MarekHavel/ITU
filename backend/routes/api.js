var express = require('express');
var router = express.Router();
var apiController = require("../controllers/api");

// Autentizace
router.post("/auth", apiController.authenticate);

// Získání menu
router.get("/menu", apiController.menuGet);

// Získání zbývajícího kreditu
router.get("/credit", apiController.creditGet);

// Nabití kreditu
router.post("/credit", apiController.creditPost);

// Vytvoření objednávky jídla
router.post("/order/create", apiController.orderCreate);

// Zrušení objednávky jídla
router.post("/order/delete", apiController.orderDelete);

// router.post("/menu", );

// router.post("/credits", );

module.exports = router;
