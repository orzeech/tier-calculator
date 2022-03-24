package pl.orzechsoft.tiercalculator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import com.github.javafaker.Faker;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.orzechsoft.tiercalculator.config.SecurityConfig;
import pl.orzechsoft.tiercalculator.config.ValidationConfig;
import pl.orzechsoft.tiercalculator.model.customer.Customer;
import pl.orzechsoft.tiercalculator.model.exception.InvalidCustomerException;
import pl.orzechsoft.tiercalculator.model.order.OrderReport;
import pl.orzechsoft.tiercalculator.service.ClientMessageService;
import pl.orzechsoft.tiercalculator.service.CustomerService;
import pl.orzechsoft.tiercalculator.service.OrderService;
import reactor.core.publisher.Mono;

@WebFluxTest(OrderController.class)
@Import({SecurityConfig.class, ValidationConfig.class})
class OrderControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @MockBean
  OrderService orderService;

  @MockBean
  CustomerService customerService;

  @MockBean
  ClientMessageService clientMessageService;

  @Value("${server.timezone}")
  private String timezone;

  @Test
  @DisplayName("Should validate token scope")
  void shouldValidateTokenScope() {
    webTestClient
        .mutateWith(mockJwt())
        .post()
        .uri("/order")
        .exchange()
        .expectStatus()
        .isForbidden();

    verify(orderService, never()).reportOrder(any());
  }

  @Test
  @DisplayName("Should report an order")
  void shouldReportOrder() {
    String customerId = UUID.randomUUID().toString();
    Customer customer = new Customer(customerId,
        Faker.instance().name().fullName());
    when(customerService.getCustomer(customerId)).thenReturn(Mono.just(customer));
    OrderReport orderReport = new OrderReport(UUID.randomUUID().toString(), customerId,
        customer.getName(), 12345, ZonedDateTime.now(ZoneId.of(timezone)));

    webTestClient
        .mutateWith(mockJwt().jwt(jwt -> jwt.claim("scope", "order:report")))
        .post()
        .uri("/order")
        .bodyValue(orderReport)
        .exchange()
        .expectStatus()
        .isOk();

    verify(orderService, times(1)).reportOrder(orderReport);
  }

  @Test
  @DisplayName("Should return bad request if the customer does not exist")
  void shouldThrowIllegalArgumentIfCustomerDoesNotExist() {
    String customerId = UUID.randomUUID().toString();
    String customerName = "Any Name";
    OrderReport orderReport = new OrderReport(UUID.randomUUID().toString(), customerId,
        customerName, 12345, ZonedDateTime.now(ZoneId.of(timezone)));
    when(orderService.reportOrder(orderReport)).thenReturn(
        Mono.error(new InvalidCustomerException("customer does not exist")));

    webTestClient
        .mutateWith(mockJwt().jwt(jwt -> jwt.claim("scope", "order:report")))
        .post()
        .uri("/order")
        .bodyValue(orderReport)
        .exchange()
        .expectStatus()
        .isBadRequest();

    verify(orderService, times(1)).reportOrder(orderReport);
  }
}
