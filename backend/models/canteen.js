// Autor: Robin Volf (xvolfr00)

const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("canteen", {
		name: {
			type: DataTypes.STRING,
			allowNull: false,
		},
		email: {
			type: DataTypes.STRING,
		},
		phone: {
			type: DataTypes.STRING,
		},
		openingHours: {
			type: DataTypes.STRING,
			allowNull: false,
		},
		address: {
			type: DataTypes.STRING,
			allowNull: false,
		},
	});
};

