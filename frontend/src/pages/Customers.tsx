import React, {useEffect, useState} from "react";
import useHttp from "../hooks/use-http";
import {getAllCustomers} from "../lib/api";
import LoadingSpinner from "../components/LoadingSpinner/LoadingSpinner";
import CustomerList from "../components/CustomerList/CustomerList";
import {GetCustomersResponse} from "../model/GetCustomersResponse";

const Customers = () => {
  const {sendRequest, status, data, error} = useHttp(getAllCustomers, true);
  const [page, setPage] = useState(1);
  useEffect(() => {
    sendRequest(page);
  }, [sendRequest, page])

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

  const response: GetCustomersResponse = data;

  if (status === 'completed' && response.allCustomersCount === 0) {
    return (<div>No customers found.</div>);
  }

  if (status === 'completed')
    return (<div>
      <CustomerList customersData={response} page={page} setPage={setPage}/>
    </div>);
  return (<div>No data?</div>)
}

export default Customers;