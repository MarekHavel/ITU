// Autor: Robin Volf (xvolfr00)

const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("dish_category", {
		name: {
			type: DataTypes.STRING,
			allowNull: false,
		},
	});
};

