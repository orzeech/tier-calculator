package pl.orzechsoft.tiercalculator.controller;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.orzechsoft.tiercalculator.model.customer.CustomerInfo;
import pl.orzechsoft.tiercalculator.model.customer.GetCustomersResponse;
import pl.orzechsoft.tiercalculator.model.exception.CustomerDoesNotExistException;
import pl.orzechsoft.tiercalculator.model.order.GetOrdersResponse;
import pl.orzechsoft.tiercalculator.service.ClientMessageService;
import pl.orzechsoft.tiercalculator.service.CustomerService;
import pl.orzechsoft.tiercalculator.service.OrderService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;
  private final OrderService orderService;
  private final ClientMessageService clientMessageService;

  @GetMapping()
  @Timed("Get customers")
  Mono<GetCustomersResponse> getCustomers(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "100") int size) {
    return customerService.getAllCustomers(page, size);
  }

  @GetMapping("/{id}/info")
  @Timed("Get customer info")
  Mono<CustomerInfo> getCustomerInfo(@PathVariable("id") String id) {
    return customerService.getCustomerInfo(id);
  }

  @GetMapping("/{id}/orders")
  @Timed("Get customer orders")
  Mono<GetOrdersResponse> getCustomerOrders(@PathVariable("id") String id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "100") int size) {
    return customerService.getCustomer(id).switchIfEmpty(Mono.error(
            new CustomerDoesNotExistException(
                clientMessageService.getMessage("customer.does.not.exist", id))))
        .flatMap(customer -> orderService.listOrders(customer, page, size));
  }
}
