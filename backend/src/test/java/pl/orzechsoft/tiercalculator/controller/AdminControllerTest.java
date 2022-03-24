package pl.orzechsoft.tiercalculator.controller;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import com.github.javafaker.Faker;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.orzechsoft.tiercalculator.config.SecurityConfig;
import pl.orzechsoft.tiercalculator.config.ValidationConfig;
import pl.orzechsoft.tiercalculator.model.customer.Customer;
import pl.orzechsoft.tiercalculator.service.ClientMessageService;
import pl.orzechsoft.tiercalculator.service.CustomerService;
import reactor.core.publisher.Mono;

@WebFluxTest(AdminController.class)
@Import({SecurityConfig.class, ValidationConfig.class})
class AdminControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @MockBean
  CustomerService clientService;

  @MockBean
  ClientMessageService clientMessageService;

  @Test
  @DisplayName("Should validate token scope")
  void shouldValidateTokenScope() {
    webTestClient
        .mutateWith(mockJwt())
        .post()
        .uri("/admin/create-customer")
        .exchange()
        .expectStatus()
        .isForbidden();

    verify(clientService, never()).createRandomCustomer();
  }

  @Test
  @DisplayName("Should create a customer")
  void shouldCreateCustomer() {
    Customer customer = new Customer(UUID.randomUUID().toString(),
        Faker.instance().name().fullName());
    when(clientService.createRandomCustomer()).thenReturn(Mono.just(customer));

    webTestClient
        .mutateWith(mockJwt().jwt(jwt -> jwt.claim("scope", "admin")))
        .post()
        .uri("/admin/create-customer")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Customer.class)
        .isEqualTo(customer);

    verify(clientService, times(1)).createRandomCustomer();
  }

}
