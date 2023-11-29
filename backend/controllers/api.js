const asyncHandler = require("express-async-handler");
const sequelize = require("../models");
const bcrypt = require("bcrypt");
const { v4: uuidv4 } = require('uuid');
const allergen = require("../models/allergen");

// Zpracování autentikace uživatele
exports.authenticate = asyncHandler(async (req, res, next) => {

  if(req.body.email == null || req.body.password == null) {
    res.status(400).json({
      code: 2,
      message: "Chybějící parametry požadavku"
    });
    return;
  }
  
  const user = await sequelize.models.user.findOne({
    attributes: ["password", "id"],
    where: {
      email: req.body.email
    }
  });

  if(user != null && bcrypt.compareSync(req.body.password, user.password)) {
    // OK
    // Vygenerovat token, uložit do db
    const newAuthToken = uuidv4();
    await user.update({authToken: newAuthToken});
    // Poslat OK odpověď s tokenem
    res.status(200).json({
      token: newAuthToken
    });
    return;
  } else {
    // ERR
    res.status(400).json({
      code: 1,
      message: "Špatné heslo"
    });
    return;
  }
});

// Získání menu na daný den
exports.menuGet = asyncHandler(async (req, res, next) => {
  if(req.query.token == null) {
    res.status(400).json({
      code: 2,
      message: "Chybějící parametry požadavku"
    });
    return;
  }

  // Pokud nebylo datum specifikováno, použije se dnešní
  let date = req.query.date ? req.query.date : new Date().toISOString().slice(0, 10);
  
  const user = await sequelize.models.user.findOne({
    attributes: ["canteenId"],
    where: {
      authToken: req.query.token
    }
  });

  const menus = await sequelize.models.menu.findAll({
    attributes: ["id", "dishId", "pieces"],
    where: {
      canteenId: user.canteenId,
      date: date
    }
  });

  // Naplnění seznamu objektů reprezentující jednotlivé pokrmy
  let resDishes = [];
  for (let menu of menus) {
    const dish = {};
    const databaseDish = await sequelize.models.dish.findOne({ where: { id:menu.dishId } });

    // Ty jednoduché
    dish.id = databaseDish.id;
    dish.name = databaseDish.name;
    dish.weight = databaseDish.weight;

    // Ty složitější
    dish.category = (await databaseDish.getDish_category()).name;

    const allergens = await databaseDish.getAllergens();
    const allergenNames = allergens.map((al) => al.code);
    dish.allergens = allergenNames.toString().replaceAll(",", ", ");

    const itemsTaken = await menu.countUsers();
    dish.itemsLeft = menu.pieces - itemsTaken;

    resDishes.push(dish);
  }

  res.status(200).json(resDishes);
});
