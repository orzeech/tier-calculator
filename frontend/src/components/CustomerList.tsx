import {Fragment} from 'react';
import {Customer} from "../model/Customer";
import {NavLink} from "react-router-dom";

const CustomerList = (props: { customers: Customer[] }) => {
  return (
      <Fragment>
        <ul>
          {props.customers.map((customer) => (
              <li>{customer.name} <NavLink to={'/customers/' + customer.id + '/orders'}>Show
                orders</NavLink> <NavLink to={'/customers/' + customer.id + '/info'}>Show
                info</NavLink>
              </li>
          ))}
        </ul>
      </Fragment>
  );
};

export default CustomerList;