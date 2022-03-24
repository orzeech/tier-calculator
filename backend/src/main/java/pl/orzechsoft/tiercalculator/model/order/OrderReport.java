package pl.orzechsoft.tiercalculator.model.order;

import java.time.ZonedDateTime;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class OrderReport {

  @Id
  @NotBlank(message = "${order.id.empty}")
  private String orderId;
  @NotBlank(message = "${customer.id.empty}")
  private String customerId;
  @NotBlank(message = "${customer.name.empty}")
  @Size(min = 3, max = 255, message = "{customer.name.size.invalid}")
  private String customerName;
  @Positive(message = "${illegal.order.sum}")
  private int totalInCents;
  @PastOrPresent(message = "${illegal.order.date}")
  private ZonedDateTime date;

}
