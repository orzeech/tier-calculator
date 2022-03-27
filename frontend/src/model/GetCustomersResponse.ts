import {Customer} from "./Customer";

export interface GetCustomersResponse {
  customers: Customer[];
  allCustomersCount: number;
}