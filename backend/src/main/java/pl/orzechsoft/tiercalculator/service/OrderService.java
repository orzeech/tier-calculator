package pl.orzechsoft.tiercalculator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.orzechsoft.tiercalculator.model.customer.Customer;
import pl.orzechsoft.tiercalculator.model.order.OrderEntity;
import pl.orzechsoft.tiercalculator.model.order.OrderInfo;
import pl.orzechsoft.tiercalculator.model.order.OrderReport;
import pl.orzechsoft.tiercalculator.repository.OrderRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final CustomerService customerService;

  public Mono<Void> reportOrder(OrderReport order) {
    return customerService.getOrCreateCustomer(order.getCustomerId(), order.getCustomerName())
        .flatMap(customer -> Mono.fromCallable(() -> orderRepository.save(
            new OrderEntity(order.getOrderId(), order.getTotalInCents(), order.getDate(),
                customer)))).then();
  }

  public Flux<OrderInfo> listOrders(Customer customer, int page, int pageSize) {
    return Flux.fromIterable(orderRepository.findAllByCustomerOrderByDate(customer,
            PageRequest.of(page, pageSize)))
        .map(orderEntity -> new OrderInfo(orderEntity.getOrderId(),
            orderEntity.getTotalInCents(), orderEntity.getDate()));
  }
}
