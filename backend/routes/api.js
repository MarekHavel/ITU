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

// Získání informací o uživateli
router.get("/user", apiController.userGet);

// Získání ohodnocení oběda uživatele
router.get("/dish/rating", apiController.dishRatingGet);

// Ohodnotit oběd
router.post("/dish/rating", apiController.dishRatingPost);

// Získání ohodnocení jídla od ostaních lidí
router.get("/dish/rating/general", apiController.dishRatingGeneralGet);

// Smazat hodnocení
router.post("/dish/rating/delete", apiController.dishRatingDelete);

// Získání ohodnocení jídla od ostaních lidí
router.get("/dish", apiController.dishGet);

// Vytvoření objednávky jídla
router.get("/order/history", apiController.orderHistoryGet);

module.exports = router;
