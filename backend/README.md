# Jak zprovoznit u sebe backend pro API a webový server

1) Stáhnout nodeJS + npm
2) Ve složce `backend` spustit příkaz `npm install` (to stáhne všechny závislosti do podsložky `backend/node_modules`)
3) Naplnit databázi příkazem `npm run databaserestart`
4) Spustit backend pomocí `npm run serverstart`
5) Nyní běží backend na localhost:3000

Backend pro mobilní aplikaci nyní běží a webové rozhraní je dostupné v browseru na http://localhost:3000. Přihlašovací údaje pro webové rozhraní: jméno: "karel@gmail.com" heslo: "admin". Příkaz `npm run databaserestart` vytvoří testovací data na aktuální a následující týden.

# Použité knihovny

- Webové rozhraní
  - HTMX - BSD 2-Clause "Simplified" License
  - Bootstrap - MIT License

- Backend
  - všechny knihovny nainstalované do `node_modules` mají LICENSE soubor
  - jsou to:
    - "bcrypt",
    - "cookie-parser",
    - "date-fns",
    - "debug",
    - "dotenv",
    - "express",
    - "express-async-handler",
    - "express-session",
    - "morgan",
    - "pug",
    - "sequelize",
    - "sqlite3",
    - "uuid"
