# Jak zprovoznit u sebe backend

1) Pokud nemáš, stáhnout nodejs + npm
2) Ve složce `backend` spustit příkaz `npm install` (to stáhne všechny závislosti do podsložky `backend/node_modules`)
3) Naplnit databázi příkazem `npm run databaserestart`
4) Spustit backend pomocí `npm run start`
5) Nyní běží backend na localhost:3000

# Vizualizace API

- lze pomocí programu openapi-generator-cli
- příkaz `openapi-generator-cli generate -i openapi.yaml -g html` vygeneruje z definice API v yamlu formátovaný HTML dokument, kde je API popsáno


