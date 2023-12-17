// Autor: Robin Volf (xvolfr00)

const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
	sequelize.define("order");
};
