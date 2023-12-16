const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("dish_preset", {
		name: {
			type: DataTypes.STRING,
			allowNull: false,
		},
		dishIds: {
			type: DataTypes.STRING,
			allowNull: false,
		},
	});
};
