// Autor: Robin Volf (xvolfr00)

const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("dish", {
		name: {
			type: DataTypes.STRING,
			allowNull: false,
		},
		ingredients: {
			type: DataTypes.STRING,
			allowNull: false,
		},
		weight: {
			type: DataTypes.INTEGER,
			allowNull: false,
		},
		image_name: { // Jméno obrázku, jeden ze staticky poskytovaných souborů v public/images
			type: DataTypes.STRING,
		}
	});
};

