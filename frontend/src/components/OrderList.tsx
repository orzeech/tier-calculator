import {Fragment} from 'react';
import {Order} from "../model/Order";

const OrderList = (props: { orders: Order[] }) => {
  return (
      <Fragment>
        <ul>
          {props.orders.map((order) => (
              <li>Order ID: {order.orderId} for {order.totalInCents / 100}$ made
                on {new Date(order.date).toDateString()}              </li>
          ))}
        </ul>
      </Fragment>
  );
};

export default OrderList;