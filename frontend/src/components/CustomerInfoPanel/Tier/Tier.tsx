import classes from './Tier.module.css';
import {TierInfo} from "../../../model/TierInfo";

const tiers: TierInfo[] = require("../../../config/tiers.json");

const Tier = (props: { tier: number }) => {
  if (props.tier > tiers.length) {
    return <span>Error: Unknown tier? 🤷</span>
  }
  const tier = tiers[props.tier];
  return <span className={classes.tier}
               style={{color: tier.color}}>{tier.icon} {tier.name} tier</span>
}

export default Tier;