package pl.orzechsoft.tiercalculator.model.order;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetOrdersResponse {

  private List<OrderInfo> orders;
  private int allOrdersCount;
}
