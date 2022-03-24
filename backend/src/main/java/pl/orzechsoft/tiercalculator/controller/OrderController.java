package pl.orzechsoft.tiercalculator.controller;

import io.micrometer.core.annotation.Timed;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.orzechsoft.tiercalculator.model.order.OrderReport;
import pl.orzechsoft.tiercalculator.service.OrderService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  @Timed("Report a completed order")
  Mono<Void> reportOrder(@Valid @RequestBody OrderReport order) {
    return orderService.reportOrder(order);
  }
}
