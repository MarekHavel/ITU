const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("priceCategory", {
		name: {
			type: DataTypes.STRING,
			allowNull: false,
		},
	});
};

