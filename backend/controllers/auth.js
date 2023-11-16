const asyncHandler = require("express-async-handler");
const sequelize = require("../models");
const bcrypt = require("bcrypt");

// Zpracování autentikace uživatele
exports.authenticate = asyncHandler(async (req, res, next) => {

  if(req.body.email == null || req.body.password == null) {
    res.status(400).json({
      code: 2,
      message: "Chybějící parametry požadavku"
    });
    return;
  }
  
  sequelize.models.user.findOne({
    attributes: ["password", "id"],
    where: {
      email: req.body.email
    }
  })
  .then((user) => {
    if(user != null && bcrypt.compareSync(req.body.password, user.password)) {
      req.session.user = user.id;

      // Uložení session a přesměrování na hlavní stránku
      req.session.save(function (err) {
        if (err) return next(err);
        // OK
        res.sendStatus(200);
        return;
      })
    } else {
      // ERR
      res.status(400).json({
        code: 1,
        message: "Špatné heslo"
      });
      return;
    }
  })
  // .catch(() => {
  //   res.status(400).json({
  //     code: 0,
  //     message: "Nelze se připojit k databázi"
  //   })
  // })
});
