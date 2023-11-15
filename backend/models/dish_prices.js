const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("dish_price", {
		price: {
			type: DataTypes.INTEGER,
			allowNull: false,
		},
	});
};
