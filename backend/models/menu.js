// Autor: Robin Volf (xvolfr00)

const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("menu", {
		id: {
			type: DataTypes.INTEGER,
			autoIncrement: true,
			primaryKey: true
		},
		date: {
			type: DataTypes.DATEONLY,
			allowNull: false
		},
		pieces: {
			type: DataTypes.INTEGER,
			defaultValue: 100,
			allowNull: false
		},
	});
};

