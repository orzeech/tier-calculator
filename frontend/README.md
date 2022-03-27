# Tier calculator frontend app

This is a simple web application written in Typescript / React to interact with tier calculator
backend.

# Usage

Run `npm start` to start the application. It will communicate with the backend on the default
address `http://localhost:8080`.

# Architecture

This is a very simple React app with React Router routers. The main routes are set up in App.tsx.
The components are using the hook defined in `use-http.ts` and the API methods defined in `api.ts`.
There are no extra libraries being used at this moment.

# Tier configuration

Tiers are configured in [tiers.json](src/config/tiers.json) file. By modifying this file, you can
change the display of the given tier. Please make sure that the `from` and `to` values are
consistent with the backend (see [backend README](../backend/README.md)).

# TODO

- **Styling** - The styling is very basic right now
- ~~**Tables with pagination** - Although the backend supports~~
- ~~**Breaking down some components / elements** - They have too much responsibility~~
- **Tests** - The application needs solid tests
- **Login / security** - The token is hardcoded
- **Manual Order report** - Make it easier to report orders
- **Refresh button for tables**
- **Better date parsing and classes in api** - Need to avoid `new Date()`