var express = require('express');
var router = express.Router();
var authController = require("../controllers/auth");

// Autentizace
router.post("/auth", authController.authenticate);

// router.post("/menu", );

// router.post("/credits", );

module.exports = router;
