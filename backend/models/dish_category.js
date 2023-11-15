const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("dishCategory", {
		name: {
			type: DataTypes.STRING,
			allowNull: false,
		},
	});
};

