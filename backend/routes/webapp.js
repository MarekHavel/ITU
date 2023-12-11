var express = require('express');
var router = express.Router();
var webappController = require("../controllers/webapp");

router.get("/week", webappController.week);

module.exports = router;
