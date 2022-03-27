package pl.orzechsoft.tiercalculator.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.orzechsoft.tiercalculator.model.customer.Customer;
import pl.orzechsoft.tiercalculator.model.order.OrderEntity;

public interface OrderRepository extends CrudRepository<OrderEntity, String> {

  List<OrderEntity> findAllByCustomerOrderByDate(Customer customer, Pageable pageable);

  long countAllByCustomer(Customer customer);

  //Returns null if there were no orders in a given year
  @Query("SELECT sum(totalInCents) FROM OrderEntity WHERE customer = ?1 AND year(date) = ?2")
  Integer sumOrderTotalsForYear(Customer customer, int year);
}
