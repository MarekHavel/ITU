const asyncHandler = require("express-async-handler");
const sequelize = require("../models");
const { Op } = require("sequelize");
const bcrypt = require("bcrypt");
const path = require("path");
const { startOfISOWeek, setISOWeek, getISOWeek, addDays, addWeeks } = require("date-fns");
const { Sequelize } = require("sequelize");

// Funkce pro získání jmen všech jídel, které lze přidat do dané nabídky jídelny daného dne
// @param date - Datum, pro které zkoumat nabídku
// @param canteen - ID jídelny, pro kterou zkoumat nabídku
// @return [string] - Pole jmen jídel, které by šly přidat
async function getAvailableDishes(date, canteen) {
  const menus = await sequelize.models.menu.findAll({
    where: {
      date:date,
      canteenId: canteen
    }
  });

  const availableDishes = await sequelize.models.dish.findAll({
    where: {
      id: {[Op.notIn]:menus.map((menu) => menu.dishId)}
    }
  });

  return availableDishes.map((dish) => dish.name);
}

// Funkce pro vygenerování informací pro vykreslení datePickeru
// @param week - číslo týdne
// @param year - rok
// @return Objekt s informacemi pro vykreslení týdne
async function generateDayPickerInfo(week, year) {
  console.log({
    week: week,
    year: year
  })
  const weekDate = setISOWeek(new Date(year, 1, 1), week);
  let weekStartdate = startOfISOWeek(weekDate);

  // Vygenerování obsahu pro tlačítka jednotlivých dní
  const weekDayNames = ["Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek", "Sobota", "Neděle"];
  let dayButtons = [];
  for(let i = 0; i < 7; i++) {
    let dayButton = {};
    dayButton.urlDate = addDays(weekStartdate, 1).toISOString().split('T')[0]; // YYYY-MM-DD
    dayButton.dateHumanReadable = weekStartdate.toLocaleDateString("cs-CZ");
    dayButton.dayName = weekDayNames[i];

    dayButtons.push(dayButton);
    weekStartdate = addDays(weekStartdate, 1);
  }

  // Vygenerování obsahu pro tlačítka předchozího a následujícího týdne
  const nextWeekNumber = getISOWeek(weekStartdate).toString().trim().padStart(2, "0");
  const prevWeekYear = weekStartdate.getFullYear();
  const prevWeekNumber = getISOWeek(addWeeks(weekStartdate, -2)).toString().trim().padStart(2, "0");
  const nextWeekYear = weekStartdate.getFullYear();

  const resData = {
    dayButtons: dayButtons,
    selectedWeek: year.toString() + "-W" + week.toString().trim().padStart(2, "0"),
    nextWeek: nextWeekYear.toString() + "-W" + nextWeekNumber,
    prevWeek: prevWeekYear.toString() + "-W" + prevWeekNumber,
  };

  console.log(resData);

  return resData;
}

// Funkce pro obsluhu vstupního bodu stránky
// Neautentizovanému uživateli zobrazí přihlášení, autentetizovaného přihlásí
exports.index = asyncHandler(async (req, res, next) => {
  if(!req.session.userId) { // Bez session
    res.render("login.pug");
    return;
  } else {
    const user = await sequelize.models.user.findByPk(req.session.userId);
    if(!user) { // Session bez autentizace
      req.session.userId = null;
      res.render("login.pug");
      return;
    } else { // Session s platnou autentizací - rovnou zobrazíme předvyplněný datePicker
      const today = new Date(); // Dnešek
      res.render("loggedIn.pug", await generateDayPickerInfo(getISOWeek(today), today.getFullYear()))
      return;
    }
  }
})

exports.week = asyncHandler(async (req, res, next) => {
  req.query.week = req.query.week.trim();
  if(!req.query.week) {
    res.render("datePickerError", {
      errorMessage: "Špatný formát týdne"
    })
    return;
  }

  // Regex pro reprezentaci HTML Week Stringu
  // https://developer.mozilla.org/en-US/docs/Web/HTML/Date_and_time_formats#week_strings 
  const week_regex = /^\d{4}-W\d{2}$/;
  if(!week_regex.test(req.query.week)) {
    res.render("datePickerError", {
      originalInput: req.query.week,
      errorMessage: "Špatný formát týdne, správný příklad: 2023-W16"
    })
    return;
  }

  const year = Number(req.query.week.split("-W")[0]);
  const weekNumber = Number(req.query.week.split("-W")[1]);

  if(weekNumber > 53) {
    res.render("datePickerError", {
      originalInput: req.query.week,
      errorMessage: "Špatné číslo týdne, musí být mezi 1 a 53"
    })
    return;
  }

  res.render("datePickerWithMessage", await generateDayPickerInfo(weekNumber, year));
});

exports.day = asyncHandler(async (req, res, next) => {
  const day = req.params.day;
  const user = await sequelize.models.user.findByPk(req.session.userId);

  const menus = await sequelize.models.menu.findAll({where: {
    date: day,
    canteenId: user.canteenId
  }})

  const resMenus = await Promise.all(menus.map(
    async (menu) => ({
      name: (await menu.getDish()).name,
      count: menu.pieces,
      id: menu.id
    })
  ));

  const availableDishes = await getAvailableDishes(req.params.day, user.canteenId);

  const info = {
      canteenId: user.canteenId,
      date: day
  };
  console.log(info);

  res.render("dishes", {
    dishesMenu: resMenus,
    availableDishes: availableDishes,
    createDishInfo: info
  })
});

exports.deleteMenu = asyncHandler(async (req, res, next) => {
  const menuId = req.params.menuId;

  const menu = await sequelize.models.menu.findByPk(menuId)
  if(menu) {
    const date = menu.date;
    const canteen = menu.canteenId;

    await menu.destroy()
    const availableDishes = await getAvailableDishes(date, canteen);

    res.render("dishDatalist", {availableDishes: availableDishes});
    return;
  } else {
    res.status(400).end();
  }
})

exports.addMenu = asyncHandler(async (req, res, next) => {
  const date = req.params.date;
  const canteenId = req.params.canteenId;

  const dish = await sequelize.models.dish.findOne({
    where: {
      name: req.body.dishName
    }
  })

  if(!dish) { // Chybný vstup
    res.render("addDishError", {
      errorMessage: "Jídlo '" + req.body.dishName + "' není v databázi"
    })
    return;
  }

  const dishAmount = Number(req.body.dishAmount);

  if(!Number.isInteger(dishAmount) || dishAmount <= 0 ||  dishAmount> 1000) {
    res.render("addDishError", {
      errorMessage: "Počet kusů musí být kladné číslo menší než 1000"
    })
    return;
  }

  if(await sequelize.models.menu.findOne({ where: {
    date: req.params.date,
    canteenId: req.params.canteenId,
    dishId: dish.id,
  }})) { // Nabídka tohoto jídla již existuje
    res.render("addDishError", {
      errorMessage: "Jídlo '" + req.body.dishName + "' již v nabídce dne je. Pokud jej chcete změnit, nejprve je smažte"
    })
    return;
  }

  const menu = await sequelize.models.menu.create({
    date: date,
    pieces: req.body.dishAmount,
    canteenId: canteenId,
    dishId: dish.id
  })

  res.render("addDish", {
    dish: {
      name: dish.name,
      count: req.body.dishAmount,
      id: menu.id
    },
    datalistDishes: await getAvailableDishes(date, canteenId),
    successMessage: "Pokrm '" + req.body.dishName + "' byl úspěšně přidán"
  })
})

exports.login = asyncHandler(async (req, res, next) => {
  const email = req.body.email;
  const password = req.body.password;

  const user = await sequelize.models.user.findOne({
    attributes: ["password", "id"],
    where: {
      email: email
    }
  });

  if(user != null && bcrypt.compareSync(password, user.password)) {
    // OK
    req.session.userId = user.id;

    // Uložení session a přesměrování na hlavní stránku
    req.session.save(async function (err) {
      if (err) return next(err);
      const today = new Date(); // Dnešek
      res.render("loginSuccess.pug", await generateDayPickerInfo(getISOWeek(today), today.getFullYear()))
      return;
    })
  } else {
    res.render("loginFailure")
  }

})

exports.logout = asyncHandler(async (req, res, next) => {
  req.session.userId = null;

  // Uložení session a přesměrování na hlavní stránku
  req.session.save(function (err) {
    if (err) return next(err);
    res.set("HX-Redirect", "/").end();
    return;
  })
})
