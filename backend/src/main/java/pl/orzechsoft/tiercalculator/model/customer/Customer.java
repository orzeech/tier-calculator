package pl.orzechsoft.tiercalculator.model.customer;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

  @Id
  private String id;

  @Size(min = 3, max = 255, message = "{customer.name.size.invalid}")
  private String name;
}