const asyncHandler = require("express-async-handler");
const sequelize = require("../models");
const bcrypt = require("bcrypt");
const { startOfISOWeek, setISOWeek, getISOWeek, addDays, addWeeks } = require("date-fns");

// Funkce pro získání data začátku týdne
// @param week - formát "YYYY-WNN", kde NN je číslo 1..53
// @return Date - datum začátku týdne (null při chybě)
function getWeekStart(week) {
  const re = /^\d{4}-W\d{2}$/;
  if(!re.test(week)) {
    return null;
  }

  const year = Number(week.split("-W")[0]);
  const weekNumber = Number(week.split("-W")[1]);

  if(weekNumber > 53) {
    return null;
  }

  const weekStart = setISOWeek(new Date(year, 1, 1), weekNumber);

  return startOfISOWeek(weekStart);
}

// TODO: Přidej do middleware-u funkci pro autorizaci uživatelů

exports.week = asyncHandler(async (req, res, next) => {
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

  const weekDate = setISOWeek(new Date(year, 1, 1), weekNumber);
  let weekStartdate = startOfISOWeek(weekDate);
  console.log(weekStartdate)
  // weekStartdate.setHours(12);

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
  const nextWeekNumber = getISOWeek(weekStartdate).toString().padStart(2, "0");
  const prevWeekNumber = getISOWeek(addWeeks(weekStartdate, -2)).toString().padStart(2, "0");

  res.render("datePicker", {
    dayButtons: dayButtons,
    selectedWeek: req.query.week,
    nextWeek: nextWeek = year.toString() + "-W" + nextWeekNumber,
    prevWeek: prevWeek = year.toString() + "-W" + prevWeekNumber,
  });
});

exports.day = asyncHandler(async (req, res, next) => {
  const day = req.params.day;

  const menus = await sequelize.models.menu.findAll({where: {
    date: day,
    //TODO canteen ID z přihlášení
  }})

  const resMenus = await Promise.all(menus.map(
    async (menu) => ({
      name: (await menu.getDish()).name,
      count: menu.pieces,
      id: menu.id
    })
  ));

  res.render("dishes", {
    dishesMenu: resMenus
  })
});

exports.deleteMenu = asyncHandler(async (req, res, next) => {
  const menuId = req.params.menuId;

  const menu = await sequelize.models.menu.findByPk(menuId)

  await menu.destroy()

  res.status(200).end();
})
