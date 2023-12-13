var express = require('express');
var session = require('express-session');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

// Natáhne do process.env proměnné z .env souboru
require('dotenv').config();

var apiRouter = require('./routes/api');
var webappRouter = require('./routes/webapp');

var app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');


// Nastavení sessions
app.use("/webapp", session({
  secret: process.env.SESSION_SECRET,
  resave: false,
  saveUninitialized: false,
  cookie: {
    maxAge: 24 * 60 * 60 * 1000 // 1 den v ms
  }
}))
app.use("/", session({
  secret: process.env.SESSION_SECRET,
  resave: false,
  saveUninitialized: false,
  cookie: {
    maxAge: 24 * 60 * 60 * 1000 // 1 den v ms
  }
}))

app.use('/api', apiRouter);
app.use('/webapp', webappRouter);

// Vytvoření databáze
// const sequelize = require("./models");
// sequelize.sync({ alter: true })
//   .then(() => sequelize.close());

module.exports = app;
