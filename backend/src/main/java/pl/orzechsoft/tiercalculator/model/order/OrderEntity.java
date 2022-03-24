package pl.orzechsoft.tiercalculator.model.order;

import java.time.ZonedDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.orzechsoft.tiercalculator.model.customer.Customer;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

  @Id
  private String orderId;
  private int totalInCents;
  private ZonedDateTime date;
  @ManyToOne
  private Customer customer;
}


