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
    attributes: ["id", "canteenId"],
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

    dish.ordered = await menu.hasUser(user);

    resDishes.push(dish);
  }

  res.status(200).json(resDishes);
});

// Získání zbývajícího kreditu uživatele
exports.creditGet = asyncHandler(async (req, res, next) => {

  if(req.query.token == null) {
    res.status(400).json({
      code: 2,
      message: "Chybějící parametry požadavku"
    });
    return;
  }

  const user = await sequelize.models.user.findOne({
    attributes: ["credit"],
    where: {
      authToken: req.query.token
    }
  });

  if(user) {
    res.status(200).json({
      credit: user.credit
    });
  } else { // Neplatný authToken
    res.status(400).json({
      code: 1,
      message: "Neplatný autentizační token"
    });
  }
});


// Nabití kreditu uživatele
exports.creditPost = asyncHandler(async (req, res, next) => {

  if(req.body.token == null || req.body.credit == null) {
    res.status(400).json({
      code: 2,
      message: "Chybějící parametry požadavku"
    });
    return;
  }

  // Kontrola, jestli je credit kladné celé číslo
  if(!Number.isInteger(req.body.credit) || req.body.credit < 1) {
    res.status(400).json({
      code: 1,
      message: "Kredit není celé kladné číslo"
    });
    return;
  }

  const user = await sequelize.models.user.findOne({
    where: {
      authToken: req.body.token
    }
  });

  if(user) {
    const userCredit = user.credit;
    await user.update({
      credit: userCredit + req.body.credit
    })
    res.status(200).end();
  } else { // Neplatný authToken
    res.status(400).json({
      code: 1,
      message: "Neplatný autentizační token"
    });
  }
});

// Objednání jídla
exports.orderCreate = asyncHandler(async (req, res, next) => {

  if(req.body.token == null || req.body.dishId == null) {
    res.status(400).json({
      code: 2,
      message: "Chybějící parametry požadavku"
    });
    return;
  }

  const user = await sequelize.models.user.findOne({
    attributes: ["id", "canteenId"],
    where: {
      authToken: req.body.token
    }
  });

  if(user) {
    const date = req.body.date ? req.body.date : new Date().toISOString().slice(0, 10);
    const menu = await sequelize.models.menu.findOne({
      where: {
        canteenId: user.canteenId,
        dishId: req.body.dishId,
        date: date
      }
    })

    if(!menu) {
      res.status(400).json({
        code: 1,
        message: "Takové menu neexistuje"
      });
      return;
    }

    const itemsTaken = await menu.countUsers();
    if(itemsTaken >= menu.pieces) {
      res.status(400).json({
        code: 1,
        message: "Jídlo je vyprodáno"
      });
      return;
    }

    await menu.addUser(user);

    res.status(200).end();
  } else { // Neplatný authToken
    res.status(400).json({
      code: 1,
      message: "Neplatný autentizační token"
    });
  }
});

// Objednání jídla
exports.orderDelete = asyncHandler(async (req, res, next) => {

  if(req.body.token == null || req.body.dishId == null) {
    res.status(400).json({
      code: 2,
      message: "Chybějící parametry požadavku"
    });
    return;
  }

  const user = await sequelize.models.user.findOne({
    attributes: ["id", "canteenId"],
    where: {
      authToken: req.body.token
    }
  });

  if(user) {
    const date = req.body.date ? req.body.date : new Date().toISOString().slice(0, 10);
    const menu = await sequelize.models.menu.findOne({
      where: {
        canteenId: user.canteenId,
        dishId: req.body.dishId,
        date: date
      }
    })

    if(!menu) {
      res.status(400).json({
        code: 1,
        message: "Takové menu neexistuje"
      });
      return;
    }

    // Pokud objednávka existuje, zrušíme ji, pokud ne, pošleme chybu
    if(await menu.hasUser(user)) {
      await menu.removeUser(user);
      res.status(200).end();
      return;
    } else {
      res.status(400).json({
        code: 1,
        message: "Objednávka neexistuje"
      });
    }

  } else { // Neplatný authToken
    res.status(400).json({
      code: 1,
      message: "Neplatný autentizační token"
    });
  }
});
