var express = require('express');
var router = express.Router();
var webappController = require("../controllers/webapp");

router.get("/", webappController.index);
router.post("/login", webappController.login);
router.post("/auth/logout", webappController.logout);
router.get("/auth/week", webappController.week);
router.get("/auth/day/:day", webappController.day);
router.delete("/auth/menuItem/:menuId", webappController.deleteMenu);
router.post("/auth/menuItem/:date/:canteenId", webappController.addMenu);
router.post("/auth/preset/save/:date", webappController.savePreset);
router.post("/auth/preset/apply/:date", webappController.applyPreset);
router.post("/auth/preset/delete", webappController.deletePreset);

module.exports = router;
