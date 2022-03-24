package pl.orzechsoft.tiercalculator.controller;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.orzechsoft.tiercalculator.model.customer.Customer;
import pl.orzechsoft.tiercalculator.service.CustomerService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final CustomerService customerService;

  @PostMapping("/create-customer")
  @Timed("Create customer")
  Mono<Customer> createCustomer() {
    return customerService.createRandomCustomer();
  }
}
