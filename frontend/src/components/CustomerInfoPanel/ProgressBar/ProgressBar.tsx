import {TierInfo} from "../../../model/TierInfo";
import classes from './ProgressBar.module.css';

const tiers: TierInfo[] = require("../../../config/tiers.json");

const ProgressBar = (props: { tier: number, totalSpent: number }) => {
  if (props.tier > tiers.length) {
    return <span>Error: Unknown tier? 🤷</span>
  }
  const tierInfo = tiers[props.tier];
  const completed = tierInfo.to === 0 ? 100 : Math.floor((props.totalSpent - tierInfo.from) / (tierInfo.to - tierInfo.from));
  return <div>
    <span className={classes.from}>${tierInfo.from / 100}</span>
    <div className={classes.container}>
      <div className={classes.filler} style={{width: `${completed}%`}}>
        <span className={classes.label}>{`${completed}%`}</span>
      </div>
    </div>
    <span className={classes.to}>${tierInfo.to === 0 ? "∞" : tierInfo.to / 100}</span>
  </div>
}

export default ProgressBar;