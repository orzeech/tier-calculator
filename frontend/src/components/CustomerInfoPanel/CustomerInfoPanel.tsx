import {CustomerInfo} from "../../model/CustomerInfo";
import Tier from "./Tier/Tier";
import ProgressBar from "./ProgressBar/ProgressBar";
import classes from './CustomerInfoPanel.module.css';

const CustomerList = (props: { customerInfo: CustomerInfo }) => {
  return (
      <>
        <h1>{props.customerInfo.customerName}</h1>
        <div>
          Customer current
          tier: <Tier tier={props.customerInfo.currentTier}/>
          <div className={classes.spendingPanel}>
            <ProgressBar tier={props.customerInfo.currentTier}
                         totalSpent={props.customerInfo.totalSpent}/></div>
          {props.customerInfo.downgradeTier &&
          <div>The customer needs to spend
            ${props.customerInfo.toSpendToAvoidDowngrade / 100} by {new Date(props.customerInfo.downgradeDate).toDateString()} to
            avoid downgrade
            to <Tier tier={props.customerInfo.downgradeTier}/></div>}
        </div>
        <div className={classes.spendingPanel}>
          Total spent since {new Date(props.customerInfo.tierStartDate).toDateString()}:
          <span className={classes.amount}> ${props.customerInfo.totalSpent / 100}.</span>
          {props.customerInfo.toSpendToReachNextTier > 0 && (
              <div>To reach <Tier tier={props.customerInfo.currentTier + 1}/>, the customer needs to
                spend <span
                    className={classes.amount}> ${props.customerInfo.toSpendToReachNextTier / 100}</span> more.
              </div>)}
        </div>
      </>
  );
};

export default CustomerList;