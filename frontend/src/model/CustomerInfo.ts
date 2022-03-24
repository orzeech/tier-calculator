export interface CustomerInfo {
  currentTier: number
  tierStartDate: Date;
  totalSpent: number;
  toSpendToReachNextTier: number;
  downgradeTier: number;
  downgradeDate: Date;
  toSpendToAvoidDowngrade: number;
}