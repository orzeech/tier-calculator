import React, {useEffect} from "react";
import {match, useRouteMatch} from "react-router-dom";
import useHttp from "../hooks/use-http";
import {getCustomerInfo} from "../lib/api";
import LoadingSpinner from "../components/LoadingSpinner/LoadingSpinner";
import CustomerInfoPanel from "../components/CustomerInfoPanel/CustomerInfoPanel";

const CustomerInfo = () => {
  const customMatch: match<{ customerId: string }> | null = useRouteMatch('/customers/:customerId/info')
  const customerId = customMatch?.params.customerId;
  const {sendRequest, status, data, error} = useHttp(getCustomerInfo, true);

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
    return <p>An error occurred during fetching the customer info: {error}</p>
  }
  if (status === 'completed' && (!data || data.length === 0)) {
    return (<div>No info for: {customerId} found.</div>);
  }
  if (status === 'completed') {
    return (<div>
      <CustomerInfoPanel customerInfo={data}/>
    </div>);
  }
  return (<div>No data?</div>)
}

export default CustomerInfo;