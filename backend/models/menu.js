const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("menu", {
		date: {
			type: DataTypes.DATE,
			allowNull: false,
		},
	});
};

