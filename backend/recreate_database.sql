-- Autor: Robin Volf (xvolfr00)

-- Vyčištění databáze
DROP TABLE IF EXISTS Orders;
DROP TABLE IF EXISTS Menus;
DROP TABLE IF EXISTS DishRatings;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Canteens;
DROP TABLE IF EXISTS Dishes;

-- Vytvoření tabulek

CREATE TABLE Users (
    UserId integer AUTO_INCREMENT,
    Name varchar(255) NOT NULL,
    Email varchar(255) NOT NULL,
    PasswordHash varchar(255) NOT NULL,
    Credit integer,
    -- Nějaké nastavení? nebo to lokálně?
    PRIMARY KEY (UserId)
);

CREATE TABLE Canteens (
    CanteenId integer AUTO_INCREMENT,
    Name varchar(255) NOT NULL,
    Email varchar(255) NOT NULL,
    PhoneNumber varchar(255) NOT NULL,
    OpeningHours varchar(255) NOT NULL, -- Netuším formát
    Address varchar(255) NOT NULL,
    PRIMARY KEY (CanteenId)
);

CREATE TABLE Dishes (
    DishId integer AUTO_INCREMENT,
    Name varchar(255) NOT NULL,
    Ingredients varchar(255) NOT NULL,
    Allergens varchar(255),
    Type varchar(255) NOT NULL,
    Price integer NOT NULL, -- Netuším formát
    -- Image? Našel jsem info, že je lepší uložit jen referenci a mít to ve FS
    PRIMARY KEY (DishId)
);

CREATE TABLE Menus (
    DishId integer,
    CanteenId integer,
    Date date,
    PRIMARY KEY (DishId, CanteenId, Date),
    FOREIGN KEY (DishId) REFERENCES Dishes(DishId),
    FOREIGN KEY (CanteenId) REFERENCES Canteens(CanteenId)
);

CREATE TABLE DishRatings (
    UserId integer,
    DishId integer,
    Start integer,
    PRIMARY KEY (UserId, DishId),
    FOREIGN KEY (DishId) REFERENCES Dishes(DishId),
    FOREIGN KEY (UserId) REFERENCES Users(UserId)
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

INSERT INTO Users (Name, Email, PasswordHash, Credit) -- Pro přihlášení novak345@gmail.com heslo
VALUES ("Jan Novák", "novak345@gmail.com", "$2y$10$Lolv0oYdErm7gVEQGbgBCeq2yWVsORcoEgiupN24RVM0477fORzaG", 1000000);

INSERT INTO Users (Name, Email, PasswordHash, Credit) -- Pro přihlášení novotnyboi829@gmail.com 12345
VALUES ("Josef Novotný", "novotnyboi829@gmail.com", "$2y$10$U1UFeaYQfxxrdIntnY2x6eWLsKMTJt8z.Oi/Fxipfqt7o5WA1QUky", 78);

INSERT INTO Canteens(Name, Email, PhoneNumber, OpeningHours, Address)
VALUES ("Jídelna1", "jidelna1@jidelnybrno.com", "+420598927106", "idkxd", "Purkyňova 2640/93, 61200 Brno - Královo Pole, Česko");
INSERT INTO Canteens(Name, Email, PhoneNumber, OpeningHours, Address)
VALUES ("Jídelna2", "jidelna2@jidelnybrno.com", "+420599999999", "idktakyxd", "Purkyňova 2640/94, 61200 Brno - Královo Pole, Česko");

INSERT INTO Dishes(Name, Ingredients, Allergens, Type, Price)
VALUES ("Kuřecí plátek hodně na divoko", "Kuře, moře, stavení", "Alergen1, alergen2", "Hlavní jídlo", 68);
INSERT INTO Dishes(Name, Ingredients, Type, Price)
VALUES ("Kuřecí vývar, vlažný", "Kuře, voda, nudle", "Polévka", 20);
