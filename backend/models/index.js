const Sequelize = require('sequelize');

const sequelize = new Sequelize({
  dialect:"sqlite",
  storage:"database/database.db",
})

const modelDefiners = [
  require('./allergen.js'),
  require('./canteen.js'),
  require('./dish_category.js'),
  require('./dish.js'),
  require('./price_category.js'),
  require('./user.js'),
  // Přemosťovací tabulky
  require('./dish_rating.js'),
  require('./menu.js'),
  require('./dish_prices.js'),
];

// Vytvoření tabulek
for (const modelDefiner of modelDefiners) {
  modelDefiner(sequelize);
}

const { allergen, canteen, dish_category, dish, price_category, user, dish_rating, menu, dish_price } = sequelize.models;

// Vytvoření vztahů

// Vztahy 1:N
user.belongsTo(price_category);
price_category.hasMany(user);

user.belongsTo(canteen);
canteen.hasMany(user);

dish.belongsTo(dish_category);
dish_category.hasMany(dish);

// Vztahy N:N
price_category.belongsToMany(dish, {through: dish_price});
dish.belongsToMany(price_category, {through: dish_price});

user.belongsToMany(dish, {through: dish_rating});
dish.belongsToMany(user, {through: dish_rating});

allergen.belongsToMany(dish, {through: "allergen_in_dish"});
dish.belongsToMany(allergen, {through: "allergen_in_dish"});

user.belongsToMany(menu, {through: "order"});
menu.belongsToMany(user, {through: "order"});

// Spešl: modelování N:N vztahu jako 1:N:1 - Sequelize nedovede dělat neunikátní N:N vztahy
canteen.hasMany(menu);
menu.belongsTo(canteen);
dish.hasMany(menu);
menu.belongsTo(dish);

module.exports = sequelize; // Export připraveného modelu
