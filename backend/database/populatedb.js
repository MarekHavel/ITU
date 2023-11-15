#! /usr/bin/env node

const sequelize = require("../models");
const models = sequelize.models;

main().catch((err) => console.log(err));

async function main() {
  await sequelize.sync({force: true});

  // Tabulky bez vztahů:
  const canteen1 = await models.canteen.create({
    name: "Jídelna1",
    email: "jidelna1@jidelnybrno.cz",
    phone: "+420598927106",
    openingHours: "idkxd",
    address: "Purkyňova 2640/93, 61200 Brno Královo Pole, Česko",
  });
  
  const studentCategory = await models.price_category.create({name: "Student"});
  const externCategory = await models.price_category.create({name: "Externista"});

  const soup = await models.dish_category.create({name: "Polévka"});
  const main = await models.dish_category.create({name: "Hlavní jídlo"});
  const sideDish = await models.dish_category.create({name: "Příloha"});

  const gluten = await models.allergen.create({name: "Lepek", code: 1});
  const crustacean = await models.allergen.create({name: "Korýši", code: 2});
  const eggs = await models.allergen.create({name: "Vejce", code: 3});
  const fish = await models.allergen.create({name: "Ryby", code: 4});
  const peanuts = await models.allergen.create({name: "Arašídy", code: 5});
  const soy = await models.allergen.create({name: "Sója", code: 6});
  const milk = await models.allergen.create({name: "Mléko", code: 7});
  const shellFruits = await models.allergen.create({name: "Skořápkové plody", code: 8});
  const celery = await models.allergen.create({name: "Celer", code: 9});
  const mustard = await models.allergen.create({name: "Hořčice", code: 10});
  const sesame = await models.allergen.create({name: "Sezam", code: 11});
  const sulfurAndSulphites = await models.allergen.create({name: "Oxid siřičitý a siřičitany", code: 12});
  const wolfBob = await models.allergen.create({name: "Vlčí bob", code: 13});
  const molluscs = await models.allergen.create({name: "Měkkýši", code: 14});

  const user1 = await models.user.create({
    username: "Jan Novák",  
    email: "jan@novak.com",  
    password: "heslo",  // Custom setter to zahešuje
    credit: "420",  
    role: "customer",  
  });

  await user1.setCanteen(canteen1);
  await user1.setPrice_category(studentCategory);

  const platek = await models.dish.create({name: "Kuřecí plátek na divoko",
    ingredients: "Kuřecí maso, sůl, kmín, řepkový olej",
    weight: 200
  });
  await platek.addAllergen(gluten);
  await platek.setDish_category(main);

  const lusk = await models.dish.create({name: "Plněný paprikový lusk",
    ingredients: "Paprika, vepřové maso, kmín, cibule",
    weight: 220
  });
  await lusk.addAllergen(gluten);
  await lusk.setDish_category(main);

  const svickova = await models.dish.create({name: "Svíčková na smetaně",
    ingredients: "Vepřové maso, mrkev, petržel, smetana, celer",
    weight: 350
  });
  await svickova.addAllergens([gluten, eggs, milk, mustard]);
  await svickova.setDish_category(main);

  const ptacek = await models.dish.create({name: "Španělský ptáček",
    ingredients: "Hovězí maso, slanina, salám, okurky, vejce, hladká mouka",
    weight: 250
  });
  await ptacek.addAllergens([gluten, eggs, mustard]);
  await ptacek.setDish_category(main);

  const buchty = await models.dish.create({name: "Dukátové buchtičky s krémem",
    ingredients: "Hladká mouka, mléko, cukr, žloutek, skořice",
    weight: 270
  });
  await buchty.addAllergens([gluten, eggs, milk]);
  await buchty.setDish_category(main);

  const vyvar = await models.dish.create({name: "Vývar s nudlemi",
    ingredients: "Kuřecí vývar, nudle z pšeničné mouky",
    weight: 150
  });
  await vyvar.addAllergens([gluten, eggs]);
  await vyvar.setDish_category(soup);

  const drstkova = await models.dish.create({name: "Dršťková",
    ingredients: "Dršťky, cibule, hladká mouka, česnek",
    weight: 150
  });
  await drstkova.addAllergens([gluten, eggs]);
  await drstkova.setDish_category(soup);

  const ryze = await models.dish.create({name: "Rýže",
    ingredients: "Jasmínová rýže",
    weight: 100
  });
  await ryze.addAllergen(gluten);
  await ryze.setDish_category(sideDish);

  const hranolky = await models.dish.create({name: "Hranolky",
    ingredients: "Brambory, olej, sůl",
    weight: 100
  });
  await hranolky.addAllergen(gluten);
  await hranolky.setDish_category(sideDish);

  const kase = await models.dish.create({name: "Bramborová kaše",
    ingredients: "Brambory, Mléko",
    weight: 100
  });
  await kase.addAllergen(gluten);
  await kase.setDish_category(sideDish);

  const knedlik = await models.dish.create({name: "Knedlík",
    ingredients: "3, hladká mouka",
    weight: 100
  });
  await knedlik.addAllergen(gluten);
  await knedlik.setDish_category(sideDish);

  //TODO:
  // DishPrices
  // Menus
  // Orders?
  await sequelize.close();
}
