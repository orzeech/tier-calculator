import classes from './Tier.module.css';

const Tier = (props: { tier: number }) => {
  switch (props.tier) {
    case 0:
      return <span className={`${classes.tier} ${classes.bronze}`}>🥉 Bronze tier</span>
    case 1:
      return <span className={`${classes.tier} ${classes.silver}`}>🥈 Silver tier</span>
    case 2:
      return <span className={`${classes.tier} ${classes.gold}`}>🥇 Gold tier</span>
    case 3:
      return <span className={`${classes.tier} ${classes.platinum}`}>🏅 Platinum tier</span>
    default:
      return <span>Unknown? 🤷</span>
  }
}

export default Tier;