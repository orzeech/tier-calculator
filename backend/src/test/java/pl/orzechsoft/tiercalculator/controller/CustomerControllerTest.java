package pl.orzechsoft.tiercalculator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import com.github.javafaker.Faker;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
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
import pl.orzechsoft.tiercalculator.model.customer.CustomerInfo;
import pl.orzechsoft.tiercalculator.model.customer.GetCustomersResponse;
import pl.orzechsoft.tiercalculator.model.exception.CustomerDoesNotExistException;
import pl.orzechsoft.tiercalculator.model.order.GetOrdersResponse;
import pl.orzechsoft.tiercalculator.model.order.OrderInfo;
import pl.orzechsoft.tiercalculator.service.ClientMessageService;
import pl.orzechsoft.tiercalculator.service.CustomerService;
import pl.orzechsoft.tiercalculator.service.OrderService;
import reactor.core.publisher.Mono;

@WebFluxTest(CustomerController.class)
@Import({SecurityConfig.class, ValidationConfig.class})
class CustomerControllerTest {

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
        .get()
        .uri("/customer")
        .exchange()
        .expectStatus()
        .isForbidden();

    verify(orderService, never()).reportOrder(any());
  }

  @Test
  @DisplayName("Should list customers")
  void shouldListCustomers() {
    String customerId = UUID.randomUUID().toString();
    Customer customer = new Customer(customerId,
        Faker.instance().name().fullName());
    String customer2Id = UUID.randomUUID().toString();
    Customer customer2 = new Customer(customer2Id,
        Faker.instance().name().fullName());
    GetCustomersResponse response = new GetCustomersResponse(
        List.of(customer, customer2), 2);
    when(customerService.getAllCustomers(anyInt(), anyInt())).thenReturn(
        Mono.just(response));

    webTestClient
        .mutateWith(mockJwt().jwt(jwt -> jwt.claim("scope", "customer:read")))
        .get()
        .uri("/customer")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(GetCustomersResponse.class)
        .equals(response);

    verify(customerService, times(1)).getAllCustomers(eq(0), anyInt());
  }

  @Test
  @DisplayName("Should get customer info")
  void shouldGetCustomerInfo() {
    String customerId = UUID.randomUUID().toString();
    CustomerInfo customerInfo = new CustomerInfo("Janusz Kowal", 0,
        ZonedDateTime.now(ZoneId.of(timezone)),
        12345, 222, null,
        ZonedDateTime.now(ZoneId.of(timezone)), 0);
    when(customerService.getCustomerInfo(customerId)).thenReturn(Mono.just(
        customerInfo));

    webTestClient
        .mutateWith(mockJwt().jwt(jwt -> jwt.claim("scope", "customer:read")))
        .get()
        .uri("/customer/{id}/info", customerId)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(CustomerInfo.class)
        .isEqualTo(customerInfo);

    verify(customerService, times(1)).getCustomerInfo(customerId);
  }

  @Test
  @DisplayName("Should get customer orders")
  void shouldGetCustomerOrders() {
    String customerId = UUID.randomUUID().toString();
    Customer customer = new Customer(customerId,
        Faker.instance().name().fullName());
    OrderInfo order1 = new OrderInfo("orderid1", 123452, ZonedDateTime.now(ZoneId.of(timezone)));
    OrderInfo order2 = new OrderInfo("orderid2", 1232, ZonedDateTime.now(ZoneId.of(timezone)));
    GetOrdersResponse response = new GetOrdersResponse(List.of(order1, order2), 2);
    when(orderService.listOrders(eq(customer), eq(0), anyInt())).thenReturn(
        Mono.just(response));
    when(customerService.getCustomer(customerId)).thenReturn(Mono.just(customer));

    webTestClient
        .mutateWith(mockJwt().jwt(jwt -> jwt.claim("scope", "customer:read")))
        .get()
        .uri("/customer/{id}/orders", customerId)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(GetOrdersResponse.class)
        .isEqualTo(response);

    verify(orderService, times(1)).listOrders(eq(customer), eq(0), anyInt());
  }

  @Test
  @DisplayName("Should return 404 if the customer does not exist")
  void shouldReturn404IfCustomerDoesNotExist() {
    String customerId = UUID.randomUUID().toString();
    when(customerService.getCustomer(customerId)).thenReturn(
        Mono.error(new CustomerDoesNotExistException("customer does not exist")));

    webTestClient
        .mutateWith(mockJwt().jwt(jwt -> jwt.claim("scope", "customer:read")))
        .get()
        .uri("/customer/{id}/orders", customerId)
        .exchange()
        .expectStatus()
        .isNotFound();

    verify(orderService, never()).listOrders(any(), anyInt(), anyInt());
  }
}
