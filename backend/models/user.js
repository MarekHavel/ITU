const { DataTypes } = require('sequelize');
const bcrypt = require('bcrypt');

// Zebrané z: https://github.com/sequelize/express-example/blob/master/express-main-example/sequelize/models/instrument.model.js

module.exports = (sequelize) => {
	sequelize.define("user", {
		// Id vygenerováno automaticky jako samoinkrementující se číslo
		username: {
			type: DataTypes.STRING,
			allowNull: false,
		},
		email: {
			type: DataTypes.STRING,
			allowNull: false,
		},
		password: {
			type: DataTypes.STRING,
			allowNull: false,
      set(value) { // Vlastní setter - heslo se při vložení automaticky zahešuje
        const hashedPassword = bcrypt.hashSync(value, 10);
        this.setDataValue("password", hashedPassword);
      }
		},
		credit: {
			type: DataTypes.INTEGER,
			defaultValue: 0
		},
    role: {
      type: DataTypes.ENUM("customer", "admin"),
      allowNull: false,
      defaultValue: "customer"
    },
    authToken: {
      type: DataTypes.UUID
    }
	});
};

