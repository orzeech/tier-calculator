package pl.orzechsoft.tiercalculator.model.customer;

import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CustomerInfo {

  private String customerName;
  private int currentTier;
  private ZonedDateTime tierStartDate;
  private int totalSpent;
  private int toSpendToReachNextTier;
  private Integer downgradeTier;
  private ZonedDateTime downgradeDate;
  private int toSpendToAvoidDowngrade;
}
