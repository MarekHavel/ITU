-- Autor: Robin Volf (xvolfr00)

-- Vyčištění databáze (v opačném pořadí k vytvoření kvůli FK omezením)
DROP TABLE IF EXISTS Orders;
DROP TABLE IF EXISTS Menus;
DROP TABLE IF EXISTS DishRatings;
DROP TABLE IF EXISTS DishPrices;
DROP TABLE IF EXISTS Dishes;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS PriceCategories;
DROP TABLE IF EXISTS DishCategories;
DROP TABLE IF EXISTS Canteens;

-- Vytvoření tabulek

CREATE TABLE Canteens (
    CanteenId integer AUTO_INCREMENT,
    Name varchar(255) NOT NULL,
    Email varchar(255) NOT NULL,
    PhoneNumber varchar(255) NOT NULL,
    OpeningHours varchar(255) NOT NULL, -- Netuším formát
    Address varchar(255) NOT NULL,
    PRIMARY KEY (CanteenId)
);

CREATE TABLE DishCategories(
    DishCategoryId integer AUTO_INCREMENT,
    Name varchar(255) NOT NULL,
    PRIMARY KEY (DishCategoryId)
);

CREATE TABLE PriceCategories(
    PriceCategoryId integer AUTO_INCREMENT,
    Name varchar(255) NOT NULL,
    PRIMARY KEY (PriceCategoryId)
);

CREATE TABLE Allergens(
    AllergenId integer AUTO_INCREMENT,
    Name varchar(255) NOT NULL,
    PRIMARY KEY (AllergenId)
);

CREATE TABLE Users (
    UserId integer AUTO_INCREMENT,
    CanteenId integer,
    PriceCategoryId integer,
    Name varchar(255) NOT NULL,
    Email varchar(255) NOT NULL,
    PasswordHash varchar(255) NOT NULL,
    Credit integer,
    PRIMARY KEY (UserId),
    FOREIGN KEY (CanteenId) REFERENCES Canteens(CanteenId),
    FOREIGN KEY (PriceCategoryId) REFERENCES PriceCategories(PriceCategoryId)
);

CREATE TABLE Dishes (
    DishId integer AUTO_INCREMENT,
    DishCategoryId integer,
    Name varchar(255) NOT NULL,
    Ingredients varchar(255) NOT NULL,
    Mass integer NOT NULL,
    -- Image? Našel jsem info, že je lepší uložit jen referenci a mít to ve FS
    PRIMARY KEY (DishId),
    FOREIGN KEY (DishCategoryId) REFERENCES DishCategories(DishCategoryId)
);

CREATE TABLE AllergensInDish (
    DishId integer,
    AllergenId integer,
    PRIMARY KEY (DishId, AllergenId),
    FOREIGN KEY (AllergenId) REFERENCES Allergens(AllergenId),
    FOREIGN KEY (DishId) REFERENCES Dishes(DishId)
);

CREATE TABLE DishPrices (
    DishId integer,
    PriceCategoryId integer,
    Price integer NOT NULL,
    PRIMARY KEY (DishId, PriceCategoryId),
    FOREIGN KEY (PriceCategoryId) REFERENCES PriceCategories(PriceCategoryId),
    FOREIGN KEY (DishId) REFERENCES Dishes(DishId)
);

CREATE TABLE DishRatings (
    UserId integer,
    DishId integer,
    Stars integer,
    PRIMARY KEY (UserId, DishId),
    FOREIGN KEY (DishId) REFERENCES Dishes(DishId),
    FOREIGN KEY (UserId) REFERENCES Users(UserId)
);

CREATE TABLE Menus (
    DishId integer,
    CanteenId integer,
    Date date,
    PRIMARY KEY (DishId, CanteenId, Date),
    FOREIGN KEY (DishId) REFERENCES Dishes(DishId),
    FOREIGN KEY (CanteenId) REFERENCES Canteens(CanteenId)
);

CREATE TABLE Orders(
    UserId integer,
-- Primární klíč Menu
    DishId integer,
    CanteenId integer,
    Date date,
--
    PRIMARY KEY (UserId, DishId, CanteenId, Date),
    FOREIGN KEY (UserId) REFERENCES Users(UserId),
    FOREIGN KEY (DishId, CanteenId, Date) REFERENCES Menus(DishId, CanteenId, Date)
);

INSERT INTO Canteens(Name, Email, PhoneNumber, OpeningHours, Address) VALUES
("Jídelna1", "jidelna1@jidelnybrno.com", "+420598927106", "idkxd", "Purkyňova 2640/93, 61200 Brno - Královo Pole, Česko"),
("Jídelna2", "jidelna2@jidelnybrno.com", "+420599999999", "idktakyxd", "Purkyňova 2640/94, 61200 Brno - Královo Pole, Česko");

INSERT INTO DishCategories(Name) VALUES 
("Hlavní jídla"),
("Polévky"),
("Přílohy");

INSERT INTO PriceCategories(Name) VALUES
("Student"),
("Externista");

INSERT INTO Users (CanteenId, PriceCategoryID, Name, Email, PasswordHash, Credit) VALUES 
 -- Pro přihlášení novak345@gmail.com heslo
(1, 1, "Jan Novák", "novak345@gmail.com", "$2y$10$Lolv0oYdErm7gVEQGbgBCeq2yWVsORcoEgiupN24RVM0477fORzaG", 1000000),
 -- Pro přihlášení novotnyboi829@gmail.com 12345
(2, 1, "Josef Novotný", "novotnyboi829@gmail.com", "$2y$10$U1UFeaYQfxxrdIntnY2x6eWLsKMTJt8z.Oi/Fxipfqt7o5WA1QUky", 78);

-- Kategorie: 1 = hl. jídlo, 2 = polévka, 3 = příloha
INSERT INTO Dishes(DishCategoryId, Name, Ingredients, Mass) VALUES
(1, "Kuřecí plátek na divoko", "Kuřecí maso, sůl, kmín, řepkový olej", 200),
(1, "Plněný paprikový lusk", "Paprika, vepřové maso, kmín, cibule", 220),
(1, "Svíčková na smetaně", "Vepřové maso, mrkev, petržel, smetana, celer", 350),
(1, "Španělský ptáček", "Hovězí maso, slanina, salám, okurky, vejce, hladká mouka", 250),
(1, "Dukátové buchtičky s krémem", "Hladká mouka, mléko, cukr, žloutek, skořice", 270),
(2, "Vývar s nudlemi", "Kuřecí vývar, nudle z pšeničné mouky", 150),
(2, "Dršťková", "Dršťky, cibule, hladká mouka, česnek", 150),
(3, "Rýže", "Jasmínová rýže", 100),
(3, "Hranolky", "Brambory, olej, sůl", 100),
(3, "Bramborová kaše", "Brambory, Mléko", 100),
(3, "Knedlík", "3, hladká mouka", 100);

INSERT INTO Allergens(Name) VALUES
("Lepek"),
("Korýši"),
("Vejce"),
("Ryby"),
("Arašídy"),
("Sója"),
("Mléko"),
("Skořápkové plody"),
("Celer"),
("Hořčice"),
("Sezam"),
("Oxid siřičitý a siřičitany"),
("Vlčí bob"),
("Měkkýši");

INSERT INTO AllergensInDish VALUES
(1, 1),
(2, 1),
(3, 1),
(3, 3),
(3, 7),
(3, 10),
(4, 1),
(4, 3),
(4, 10),
(5, 1),
(5, 3),
(5, 7),
(6, 1),
(6, 3),
(7, 1),
(10, 7),
(11, 3);

-- Vložení cen jídla pro kategorii "student"
INSERT INTO DishPrices(DishId, PriceCategoryId, Price) VALUES
(1, 1, 54),
(2, 1, 61),
(3, 1, 80),
(4, 1, 57),
(5, 1, 44),
(6, 1, 20),
(7, 1, 20),
(8, 1, 15),
(9, 1, 22),
(10, 1, 18),
(11, 1, 20);

-- Vložení nějakých nabídek jídel pro jídelnu 1
-- Nasimulovaný týden 13.11 - 17.11
INSERT INTO Menus(DishId, CanteenId, Date) VALUES
-- Pondělí --
-- Jídelna 1
(1, 1, '2023-11-13'),
(2, 1, '2023-11-13'),
(8, 1, '2023-11-13'),
(10, 1, '2023-11-13'),
(6, 1, '2023-11-13'),
-- Jídelna 2
(2, 2, '2023-11-13'),
(3, 2, '2023-11-13'),
(9, 2, '2023-11-13'),
(11, 2, '2023-11-13'),
(7, 2, '2023-11-13'),
-- Úterý --
-- Jídelna 1
(2, 2, '2023-11-14'),
(3, 2, '2023-11-14'),
(9, 2, '2023-11-14'),
(11, 2, '2023-11-14'),
(7, 2, '2023-11-14'),
-- Jídelna 2
(1, 1, '2023-11-14'),
(2, 1, '2023-11-14'),
(8, 1, '2023-11-14'),
(10, 1, '2023-11-14'),
(6, 1, '2023-11-14'),
-- Středa --
-- Jídelna 1
(4, 1, '2023-11-15'),
(5, 1, '2023-11-15'),
(9, 1, '2023-11-15'),
(6, 1, '2023-11-15'),
-- Jídelna 2
(3, 2, '2023-11-15'),
(4, 2, '2023-11-15'),
(10, 2, '2023-11-15'),
(7, 2, '2023-11-15'),
-- Čtvrtek --
-- Jídelna 1
(3, 2, '2023-11-16'),
(4, 2, '2023-11-16'),
(10, 2, '2023-11-16'),
(8, 2, '2023-11-16'),
(7, 2, '2023-11-16'),
-- Jídelna 2
(4, 1, '2023-11-16'),
(5, 1, '2023-11-16'),
(9, 1, '2023-11-16'),
(10, 1, '2023-11-16'),
(6, 1, '2023-11-16'),
-- Pátek --
-- Jídelna 1
(1, 1, '2023-11-17'),
(2, 1, '2023-11-17'),
(8, 1, '2023-11-17'),
(10, 1, '2023-11-17'),
(6, 1, '2023-11-17'),
-- Jídelna 2
(1, 2, '2023-11-17'),
(2, 2, '2023-11-17'),
(8, 2, '2023-11-17'),
(10, 2, '2023-11-17'),
(7, 1, '2023-11-17');
