import {CustomerInfo} from "../../model/CustomerInfo";
import Tier from "./Tier/Tier";

const CustomerList = (props: { customerInfo: CustomerInfo }) => {
  return (
      <>
        <h1>{props.customerInfo.customerName}</h1>
        <p>
          Customer current
          tier: <Tier tier={props.customerInfo.currentTier}/> {props.customerInfo.downgradeTier &&
        <div>The customer needs to spend
          ${props.customerInfo.toSpendToAvoidDowngrade / 100} by {new Date(props.customerInfo.downgradeDate).toDateString()} to
          avoid downgrade
          to <Tier tier={props.customerInfo.downgradeTier}/></div>}
        </p>
        <p>
          Total spent since {new Date(props.customerInfo.tierStartDate).toDateString()}:
          ${props.customerInfo.totalSpent / 100}.
          {props.customerInfo.toSpendToReachNextTier > 0 && (
              <div>To reach <Tier tier={props.customerInfo.currentTier + 1}/>, the customer needs to
                spend ${props.customerInfo.toSpendToReachNextTier / 100} more.</div>)}
        </p>
      </>
  );
};

export default CustomerList;