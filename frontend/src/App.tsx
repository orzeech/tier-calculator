import React from 'react';
import './App.css';
import Customers from "./pages/Customers";
import {Redirect, Route, Switch} from "react-router-dom";
import MainHeader from "./components/MainHeader";
import Orders from "./pages/Orders";
import NotFound from "./pages/NotFound";
import CustomerInfo from "./pages/CustomerInfo";
import Admin from "./pages/Admin";

function App() {
  return (
      <div className="App">
        <header>
          <MainHeader/>
        </header>
        <Switch>
          <Route path="/" exact>
            <Redirect to="/customers"/>
          </Route>
          <Route path="/customers/:customerId/orders" exact>
            <Orders/>
          </Route>
          <Route path="/customers/:customerId/info" exact>
            <CustomerInfo/>
          </Route>
          <Route path="/customers">
            <Customers/>
          </Route>
          <Route path="/admin">
            <Admin/>
          </Route>
          <Route path="*">
            <NotFound/>
          </Route>
        </Switch>
      </div>
  );
}

export default App;
