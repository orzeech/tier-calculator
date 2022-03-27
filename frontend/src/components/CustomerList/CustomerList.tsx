import React from 'react';
import {NavLink} from "react-router-dom";
import {PAGE_SIZE} from "../../lib/api";
import Table from "../Table/Table";
import {GetCustomersResponse} from "../../model/GetCustomersResponse";
import {Customer} from "../../model/Customer";

const CustomerList = (props: {
  customersData: GetCustomersResponse, page: number,
  setPage: Function,
}) => {
  return (
      <>
        <Table data={props.customersData.customers}
               page={props.page}
               setPage={props.setPage}
               rowsPerPage={PAGE_SIZE}
               allPagesCount={Math.ceil(props.customersData.allCustomersCount / PAGE_SIZE)}
               headerTexts={["Name", "", ""]}
               rowTexts={[(customer: Customer) => customer.name, (customer: Customer) => <NavLink
                   to={`/customers/${customer.id}/orders`}>Show
                 orders</NavLink>, (customer: Customer) => <NavLink
                   to={`/customers/${customer.id}/info`}>Show
                 info</NavLink>]}/>
      </>
  );
};

export default CustomerList;