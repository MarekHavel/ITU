var express = require('express');
var session = require('express-session');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

// Natáhne do process.env proměnné z .env souboru
require('dotenv').config();

var apiRouter = require('./routes/api');
var webappRouter = require('./routes/webapp');
var authMiddleware = require("./controllers/middleware");

var app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');

// Nastavení sessions pro webové rozhraní
app.use("/webapp", session({
  secret: process.env.SESSION_SECRET,
  resave: false,
  saveUninitialized: false,
  cookie: {
    maxAge: 24 * 60 * 60 * 1000 // 1 den v ms
  }
}))

// Kořen se přesměruje na kořen webapp
app.get('/', function(req, res) {
  res.redirect('/webapp');
})

// Routování pro API
app.use('/api', apiRouter);

// Routování webového klienta
app.use('/webapp/auth', authMiddleware.auth); // Middleware pro autorizaci
app.use('/webapp', webappRouter);

module.exports = app;
