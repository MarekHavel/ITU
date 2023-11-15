const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("allergen", {
		name: {
			type: DataTypes.STRING,
			allowNull: false,
		},
		code: {
			type: DataTypes.INTEGER,
			allowNull: false,
		},
	});
};

