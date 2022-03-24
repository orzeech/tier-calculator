import React, {useEffect} from "react";
import {match, useRouteMatch} from "react-router-dom";
import useHttp from "../hooks/use-http";
import {getOrdersForCustomer} from "../lib/api";
import LoadingSpinner from "../components/LoadingSpinner";
import OrderList from "../components/OrderList";

const Orders = () => {
  const customMatch: match<{ customerId: string }> | null = useRouteMatch('/customers/:customerId/orders')
  const customerId = customMatch?.params.customerId;
  const {sendRequest, status, data, error} = useHttp(getOrdersForCustomer, true);

  useEffect(() => {
    sendRequest(customerId);
  }, [sendRequest, customerId])

  if (status === 'pending') {
    return (
        <div>
          <LoadingSpinner/>
        </div>
    )
  }
  if (error) {
    return <p>An error occurred during fetching of the orders for customer: {error}</p>
  }
  if (status === 'completed' && (!data || data.length === 0)) {
    return (<div>No orders for: {customerId} found.</div>);
  }
  if (status === 'completed') {
    return (<div>
      <OrderList orders={data}/>
    </div>);
  }
  return (<div>No data?</div>)
}

export default Orders;