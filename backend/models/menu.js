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
	});
};

