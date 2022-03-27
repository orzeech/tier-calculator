package pl.orzechsoft.tiercalculator.model.customer;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetCustomersResponse {

  private List<Customer> customers;
  private int allCustomersCount;
}
