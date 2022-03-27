import {Customer} from "../model/Customer";
import {CustomerInfo} from "../model/CustomerInfo";
import {GetCustomersResponse} from "../model/GetCustomersResponse";
import {GetOrdersResponse} from "../model/GetOrdersResponse";

const BACKEND_URL = 'http://localhost:8080';
const API_TOKEN = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwic2NvcGUiOlsiY3VzdG9tZXI6cmVhZCIsImFkbWluIiwib3JkZXI6cmVwb3J0Il19.UducMnEHXeNZnQXyrhPpYaOXwbwXBqPBIA4jOunptxEZXTXb6bFqM6Ueg9ymJnVsLChpSvkeCceuY1R-Sse_y10uhI3q03cOLUJKO7DNsAPqBZtmNCAIsOfzNEuwXJh5V9wwhSM58jMKj_3JcEqDbOdx9e02YxgB8SG24SCdqdCRvZiAYT_RHU0WVOJfLPFed98V0GcDqNcsRc6xxhadDabX6MSlR0ITg4yH7ViR_F7y4RulZNDgBOeE048bLmJdDlc0rgQ3Kbc0PYBLAMGuFq-YnGtn0wIYMwFyVcbpuKE2iQzFICRfzu5JYETZ9TBV7d_Mdj2nngFHAU25Klj_wg';
export const PAGE_SIZE = 7;

export async function getAllCustomers(page: number): Promise<GetCustomersResponse> {
  return await executeApiCall(`/customer?page=${page - 1}&size=${PAGE_SIZE}`, "GET", "Could not fetch customers.");
}

export async function getOrdersForCustomer(customerId: string): Promise<GetOrdersResponse> {
  return await executeApiCall(`/customer/${customerId}/orders`, "GET",
      'Could not fetch orders for customer ' + customerId);
}

export async function getCustomerInfo(customerId: string): Promise<CustomerInfo> {
  return await executeApiCall(`/customer/${customerId}/info`, "GET", 'Could not fetch orders for customer ' + customerId)
}

export async function createRandomCustomer(): Promise<Customer> {
  return await executeApiCall("/admin/create-customer", "POST", "Could not create a random customer");
}

async function executeApiCall(endpoint: string, method: string, errorMessage: string) {
  const response = await fetch(`${BACKEND_URL}${endpoint}`, {
    headers: {
      Authorization: "Bearer " + API_TOKEN
    },
    method: method
  });
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.message || errorMessage);
  }
  return data;
}
