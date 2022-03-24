import React from "react";
import {NavLink} from "react-router-dom"

import classes from './MainHeader.module.css';

export default class MainHeader extends React.Component <{}> {
  render() {
    return (
        <header className={classes.header}>
          <nav>
            <ul>
              <li>
                <NavLink activeClassName={classes.active} to="/customers">Customers</NavLink>
              </li>
              <li>
                <NavLink activeClassName={classes.active} to="/admin">Admin</NavLink>
              </li>
            </ul>
          </nav>
        </header>);
  }
}