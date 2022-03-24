package pl.orzechsoft.tiercalculator.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import pl.orzechsoft.tiercalculator.model.customer.Customer;

public interface CustomerRepository extends CrudRepository<Customer, String> {

  List<Customer> findAll(Pageable pageable);
}
