package pl.orzechsoft.tiercalculator.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.javafaker.Faker;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.orzechsoft.tiercalculator.model.customer.Customer;
import pl.orzechsoft.tiercalculator.model.order.OrderEntity;

@DataJpaTest
class OrderRepositoryTest {

  @Autowired
  OrderRepository orderRepository;

  @Autowired
  CustomerRepository customerRepository;

  @Test
  @DisplayName("Should get sum of orders for a given year")
  void shouldGetSumOfOrders() {
    String customerId = UUID.randomUUID().toString();
    String customerName = Faker.instance().name().fullName();
    Customer customer = new Customer(customerId,
        customerName);
    customerRepository.save(customer);
    orderRepository.save(new OrderEntity("abc", 1234,
        ZonedDateTime.of(2022, 3, 22, 12, 23, 0, 0, ZoneId.systemDefault()), customer));
    orderRepository.save(
        new OrderEntity("abca", 1200,
            ZonedDateTime.of(2022, 3, 12, 12, 23, 0, 0, ZoneId.systemDefault()), customer));
    orderRepository.save(
        new OrderEntity("abcas", 1300,
            ZonedDateTime.of(2022, 3, 12, 12, 23, 0, 0, ZoneId.systemDefault()), null));
    int sum = orderRepository.sumOrderTotalsForYear(customer, 2022);
    Integer sumYearNoOrders = orderRepository.sumOrderTotalsForYear(customer, 2020);

    assertEquals(1234 + 1200, sum);
    assertNull(sumYearNoOrders);
  }

}