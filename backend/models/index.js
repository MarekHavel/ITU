const Sequelize = require('sequelize');

const sequelize = new Sequelize({
  dialect:"sqlite",
  storage:"../database/database.db"
})

const modelDefiners = [
	require('allergen.js'),
	require('canteen.js'),
	require('dish_category.js'),
	require('dish.js'),
	require('price_category.js'),
	require('user.js'),
];

// Vytvoření všech nevztahových tabulek 
for (const modelDefiner of modelDefiners) {
	modelDefiner(sequelize);
}

//TODO: Vytvoření všech vztahových tabulek! a přidání vztahů do stávajících
//TODO: Naplnit sample daty (ale tu asi ne)

module.exports = sequelize; // Export připraveného modelu
