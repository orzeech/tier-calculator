import {Order} from "../../model/Order";
import {GetOrdersResponse} from "../../model/GetOrdersResponse";
import {PAGE_SIZE} from "../../lib/api";
import Table from "../Table/Table";
import React from "react";

const OrderList = (props: {
  orderData: GetOrdersResponse,
  page: number,
  setPage: Function
}) => {
  return (
      <>
        <Table data={props.orderData.orders}
               page={props.page}
               setPage={props.setPage}
               rowsPerPage={PAGE_SIZE}
               allPagesCount={Math.ceil(props.orderData.allOrdersCount / PAGE_SIZE)}
               headerTexts={["Id", "Date", "Amount"]}
               rowTexts={[(order: Order) => order.orderId,
                 (order: Order) => new Date(order.date).toDateString(),
                 (order: Order) => "$" + order.totalInCents / 100
               ]}/>
      </>
  );
};

export default OrderList;