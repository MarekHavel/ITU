const asyncHandler = require("express-async-handler");
const sequelize = require("../models");
const bcrypt = require("bcrypt");
const { startOfISOWeek, setISOWeek, addDays } = require("date-fns");

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
    //TODO nějaký template s chybovou hláškou
    res.send("Chyba požadavku kamaráde!");
  }

  let date = getWeekStart(req.query.week);
  
  if(!date) {
    //TODO nějaký template s chybovou hláškou
    res.send("Chyba formátu kamaráde!");
  }
  
  const weekDayNames = ["Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek", "Sobota", "Neděle"];
  let dayButtons = [];
  for(let i = 0; i < 7; i++) {
    let dayButton = {};
    dayButton.urlDate = date.toISOString().split('T')[0]; // YYYY-MM-DD
    date = addDays(date, 1);

    dayButton.dayName = weekDayNames[i];
    dayButtons.push(dayButton);
  }

  console.log(dayButtons);

  res.render("weekContent", {dayButtons: dayButtons});
});
