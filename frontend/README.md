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

# TODO

- **Styling** - The styling is very basic right now
- **Tables with pagination** - Although the backend supports
- **Breaking down some components / elements** - They have too much responsibility
- **Tests** - The application needs solid tests
- **Login / security** - The token is hardcoded