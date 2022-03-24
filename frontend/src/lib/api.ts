import {Customer} from "../model/Customer";
import {Order} from "../model/Order";
import {CustomerInfo} from "../model/CustomerInfo";

const BACKEND_URL = 'http://localhost:8080';
const API_TOKEN = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwic2NvcGUiOlsiY3VzdG9tZXI6cmVhZCIsImFkbWluIiwib3JkZXI6cmVwb3J0Il19.UducMnEHXeNZnQXyrhPpYaOXwbwXBqPBIA4jOunptxEZXTXb6bFqM6Ueg9ymJnVsLChpSvkeCceuY1R-Sse_y10uhI3q03cOLUJKO7DNsAPqBZtmNCAIsOfzNEuwXJh5V9wwhSM58jMKj_3JcEqDbOdx9e02YxgB8SG24SCdqdCRvZiAYT_RHU0WVOJfLPFed98V0GcDqNcsRc6xxhadDabX6MSlR0ITg4yH7ViR_F7y4RulZNDgBOeE048bLmJdDlc0rgQ3Kbc0PYBLAMGuFq-YnGtn0wIYMwFyVcbpuKE2iQzFICRfzu5JYETZ9TBV7d_Mdj2nngFHAU25Klj_wg';

export async function getAllCustomers(): Promise<Customer[]> {
  const response = await fetch(`${BACKEND_URL}/customer`, {
    headers: {
      Authorization: "Bearer " + API_TOKEN
    }
  });
  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || 'Could not fetch customers.');
  }

  const transformedCustomers: Customer[] = [];
  for (const key in data) {
    const customerObj = {
      id: key,
      ...data[key],
    };
    transformedCustomers.push(customerObj);
  }

  return transformedCustomers;
}

export async function getOrdersForCustomer(customerId: string): Promise<Order[]> {
  const response = await fetch(`${BACKEND_URL}/customer/${customerId}/orders`, {
    headers: {
      Authorization: "Bearer " + API_TOKEN
    }
  });
  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || 'Could not fetch orders for customer ' + customerId);
  }

  const transformedCustomers: Order[] = [];
  for (const key in data) {
    const orderObj = {
      id: key,
      ...data[key],
    };
    transformedCustomers.push(orderObj);
  }
  return transformedCustomers;
}

export async function getCustomerInfo(customerId: string): Promise<CustomerInfo> {
  const response = await fetch(`${BACKEND_URL}/customer/${customerId}/info`, {
    headers: {
      Authorization: "Bearer " + API_TOKEN
    }
  });
  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || 'Could not fetch orders for customer ' + customerId);
  }

  return data;
}

export async function createRandomCustomer(): Promise<Customer> {
  const response = await fetch(`${BACKEND_URL}/admin/create-customer`, {
    headers: {
      Authorization: "Bearer " + API_TOKEN
    },
    method: "POST"
  });
  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || 'Could not create a random customer');
  }

  return data;
}
