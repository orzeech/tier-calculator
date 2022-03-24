package pl.orzechsoft.tiercalculator.model.order;

import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class OrderInfo {

  private String orderId;
  private int totalInCents;
  private ZonedDateTime date;
}
