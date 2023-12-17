const asyncHandler = require("express-async-handler");
const sequelize = require("../models");

// Middleware pro zajištění autentizace uživatele
// Uživatel bez platné session bude přesměrován na přihlášení
exports.auth = asyncHandler(async (req, res, next) => {
  if(!req.session.userId) {
    res.set("HX-Redirect", "/").end();
    return;
  } else {
    const user = await sequelize.models.user.findByPk(req.session.userId);
    if(!user) {
      req.session.userId = null;
      res.set("HX-Redirect", "/").end();
      return;
    } else {
      next()
    }
  }
})

