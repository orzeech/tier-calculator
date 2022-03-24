import useHttp from "../hooks/use-http";
import {createRandomCustomer} from "../lib/api";
import LoadingSpinner from "./LoadingSpinner";
import React from "react";

const CreateCustomer = () => {
  const {sendRequest, status, data, error} = useHttp(createRandomCustomer, false);

  const createNewCustomer = () => {
    sendRequest(null)
  }

  if (status === 'pending') {
    return (
        <p>
          <LoadingSpinner/>
        </p>
    )
  }
  if (error) {
    return <p>An error occurred during fetching of the orders for customer: {error}</p>
  }
  if (status === 'completed' && (!data)) {
    return (<p><p>No customer has been created.</p>
      <button onClick={createNewCustomer}>Try again</button>
    </p>);
  }
  if (status === 'completed') {
    return (<div>
      <p>Customer with name {data.name} and id {data.id} has been created.</p>
      <p>
        <button onClick={createNewCustomer}>Create another one</button>
      </p>
    </div>);
  }
  return (<p>
    <button onClick={createNewCustomer}>Create a new customer</button>
  </p>)
}

export default CreateCustomer;