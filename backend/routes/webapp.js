var express = require('express');
var router = express.Router();
var webappController = require("../controllers/webapp");

router.get("/week", webappController.week);
router.get("/day/:day", webappController.day);
router.delete("/menuItem/:menuId", webappController.deleteMenu);

module.exports = router;
