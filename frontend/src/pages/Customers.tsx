import React, {useEffect} from "react";
import useHttp from "../hooks/use-http";
import {getAllCustomers} from "../lib/api";
import LoadingSpinner from "../components/LoadingSpinner";
import CustomerList from "../components/CustomerList";

const Customers = () => {
  const {sendRequest, status, data, error} = useHttp(getAllCustomers, true);

  useEffect(() => {
    sendRequest(null);
  }, [sendRequest])

  if (status === 'pending') {
    return (
        <div>
          <LoadingSpinner/>
        </div>
    )
  }

  if (error) {
    return <p>An error occurred during fetching of the customers: {error}</p>
  }

  if (status === 'completed' && data.length === 0)
    return (<div>No customers found.</div>);

  if (status === 'completed')
    return (<div>
      <CustomerList customers={data}/>
    </div>);
  return (<div>No data?</div>)
}

export default Customers;