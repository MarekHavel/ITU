#! /usr/bin/env node

const sequelize = require("../models");
const models = sequelize.models;

main().catch((err) => console.log(err));

async function main() {
  await sequelize.sync({force: true});

  const queryInterface = sequelize.getQueryInterface();
  queryInterface.addConstraint("menus", {
    fields: ["canteenId", "dishId", "date"],
    type: "unique",
    name: "unique_food_in_canteen_at_a_given_day"
  });

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
    authToken: "7198b575-ba08-4275-884c-89fc85b2732c" // Pro testování
  });

  await user1.setCanteen(canteen1);
  await user1.setPrice_category(studentCategory);

  const admin = await models.user.create({
    username: "Karel Pořízek",  
    email: "karel@gmail.com",  
    password: "admin",  // Custom setter to zahešuje
    role: "admin",  
  });

  await admin.setCanteen(canteen1);

  const platek = await models.dish.create({name: "Kuřecí plátek na divoko",
    ingredients: "Kuřecí maso, sůl, kmín, řepkový olej",
    weight: 200
  });
  await platek.addAllergen(gluten);
  await platek.setDish_category(main);
  await platek.addPrice_category(studentCategory, {through: { price: 54}});

  const lusk = await models.dish.create({name: "Plněný paprikový lusk",
    ingredients: "Paprika, vepřové maso, kmín, cibule",
    weight: 220
  });
  await lusk.addAllergen(gluten);
  await lusk.setDish_category(main);
  await lusk.addPrice_category(studentCategory, {through: { price: 61}});

  const svickova = await models.dish.create({name: "Svíčková na smetaně",
    ingredients: "Vepřové maso, mrkev, petržel, smetana, celer",
    weight: 350
  });
  await svickova.addAllergens([gluten, eggs, milk, mustard]);
  await svickova.setDish_category(main);
  await svickova.addPrice_category(studentCategory, {through: { price: 80}});

  const ptacek = await models.dish.create({name: "Španělský ptáček",
    ingredients: "Hovězí maso, slanina, salám, okurky, vejce, hladká mouka",
    weight: 250
  });
  await ptacek.addAllergens([gluten, eggs, mustard]);
  await ptacek.setDish_category(main);
  await ptacek.addPrice_category(studentCategory, {through: { price: 57}});

  const buchty = await models.dish.create({name: "Dukátové buchtičky s krémem",
    ingredients: "Hladká mouka, mléko, cukr, žloutek, skořice",
    weight: 270
  });
  await buchty.addAllergens([gluten, eggs, milk]);
  await buchty.setDish_category(main);
  await buchty.addPrice_category(studentCategory, {through: { price: 44}});

  const vyvar = await models.dish.create({name: "Vývar s nudlemi",
    ingredients: "Kuřecí vývar, nudle z pšeničné mouky",
    weight: 150
  });
  await vyvar.addAllergens([gluten, eggs]);
  await vyvar.setDish_category(soup);
  await vyvar.addPrice_category(studentCategory, {through: { price: 20}});

  const drstkova = await models.dish.create({name: "Dršťková",
    ingredients: "Dršťky, cibule, hladká mouka, česnek",
    weight: 150
  });
  await drstkova.addAllergens([gluten, eggs]);
  await drstkova.setDish_category(soup);
  await drstkova.addPrice_category(studentCategory, {through: { price: 20}});

  const ryze = await models.dish.create({name: "Rýže",
    ingredients: "Jasmínová rýže",
    weight: 100
  });
  await ryze.addAllergen(gluten);
  await ryze.setDish_category(sideDish);
  await ryze.addPrice_category(studentCategory, {through: { price: 15}});

  const hranolky = await models.dish.create({name: "Hranolky",
    ingredients: "Brambory, olej, sůl",
    weight: 100
  });
  await hranolky.addAllergen(gluten);
  await hranolky.setDish_category(sideDish);
  await hranolky.addPrice_category(studentCategory, {through: { price: 22}});

  const kase = await models.dish.create({name: "Bramborová kaše",
    ingredients: "Brambory, Mléko",
    weight: 100
  });
  await kase.addAllergen(gluten);
  await kase.setDish_category(sideDish);
  await kase.addPrice_category(studentCategory, {through: { price: 18}});

  const knedlik = await models.dish.create({name: "Knedlík",
    ingredients: "Hladká mouka",
    weight: 100
  });
  await knedlik.addAllergen(gluten);
  await knedlik.setDish_category(sideDish);
  await knedlik.addPrice_category(studentCategory, {through: { price: 20}});

  await models.menu.create({date: '2023-11-13', dishId: platek.id, canteenId: canteen1.id, pieces: 47});
  await models.menu.create({date: '2023-11-13', dishId: lusk.id, canteenId: canteen1.id, pieces: 44});
  await models.menu.create({date: '2023-11-13', dishId: ryze.id, canteenId: canteen1.id, pieces: 40});
  await models.menu.create({date: '2023-11-13', dishId: kase.id, canteenId: canteen1.id, pieces: 42});
  await models.menu.create({date: '2023-11-13', dishId: vyvar.id, canteenId: canteen1.id, pieces: 30});

  await models.menu.create({date: '2023-11-14', dishId: lusk.id, canteenId: canteen1.id, pieces: 35});
  await models.menu.create({date: '2023-11-14', dishId: svickova.id, canteenId: canteen1.id, pieces: 35});
  await models.menu.create({date: '2023-11-14', dishId: hranolky.id, canteenId: canteen1.id, pieces: 38});
  await models.menu.create({date: '2023-11-14', dishId: knedlik.id, canteenId: canteen1.id, pieces: 32});
  await models.menu.create({date: '2023-11-14', dishId: drstkova.id, canteenId: canteen1.id, pieces: 42});

  await models.menu.create({date: '2023-11-15', dishId: ptacek.id, canteenId: canteen1.id, pieces: 47});
  await models.menu.create({date: '2023-11-15', dishId: buchty.id, canteenId: canteen1.id, pieces: 47});
  await models.menu.create({date: '2023-11-15', dishId: ryze.id, canteenId: canteen1.id, pieces: 50});
  await models.menu.create({date: '2023-11-15', dishId: kase.id, canteenId: canteen1.id, pieces: 31});
  await models.menu.create({date: '2023-11-15', dishId: vyvar.id, canteenId: canteen1.id, pieces: 36});

  await models.menu.create({date: '2023-11-16', dishId: svickova.id, canteenId: canteen1.id, pieces: 35});
  await models.menu.create({date: '2023-11-16', dishId: ptacek.id, canteenId: canteen1.id, pieces: 43});
  await models.menu.create({date: '2023-11-16', dishId: knedlik.id, canteenId: canteen1.id, pieces: 33});
  await models.menu.create({date: '2023-11-16', dishId: hranolky.id, canteenId: canteen1.id, pieces: 42});
  await models.menu.create({date: '2023-11-16', dishId: drstkova.id, canteenId: canteen1.id, pieces: 49});

  await models.menu.create({date: '2023-11-17', dishId: platek.id, canteenId: canteen1.id, pieces: 33});
  await models.menu.create({date: '2023-11-17', dishId: lusk.id, canteenId: canteen1.id, pieces: 33});
  await models.menu.create({date: '2023-11-17', dishId: hranolky.id, canteenId: canteen1.id, pieces: 44});
  await models.menu.create({date: '2023-11-17', dishId: kase.id, canteenId: canteen1.id, pieces: 30});
  await models.menu.create({date: '2023-11-17', dishId: vyvar.id, canteenId: canteen1.id, pieces: 32});

  await sequelize.close();
}
