import {Order} from "./Order";

export interface GetOrdersResponse {
  orders: Order[];
  allOrdersCount: number;
}