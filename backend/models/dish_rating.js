const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("dish_rating", {
		stars: {
			type: DataTypes.INTEGER,
			allowNull: false,
      validate: { // Na Sequelize úrovni validuje, že hodnota je 0 - 5
        min: 0,    
        max: 5,
      }
		},
		comment: {
			type: DataTypes.TEXT,
		},
	});
};
