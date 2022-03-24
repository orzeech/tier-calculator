package pl.orzechsoft.tiercalculator.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import pl.orzechsoft.tiercalculator.model.customer.Customer;
import pl.orzechsoft.tiercalculator.model.customer.CustomerInfo;
import pl.orzechsoft.tiercalculator.model.exception.CustomerDoesNotExistException;
import pl.orzechsoft.tiercalculator.repository.CustomerRepository;
import pl.orzechsoft.tiercalculator.repository.OrderRepository;
import reactor.test.StepVerifier;

class CustomerServiceTest {

  CustomerRepository clientRepository;
  OrderRepository orderRepository;
  ClientMessageService clientMessageService;
  CustomerService service;

  @BeforeEach
  void setupMocks() {
    clientRepository = mock(CustomerRepository.class);
    orderRepository = mock(OrderRepository.class);
    clientMessageService = mock(ClientMessageService.class);

    service = new CustomerService(clientRepository, orderRepository,
        clientMessageService);
  }

  @Test
  @DisplayName("Should create a random customer")
  void shouldCreateClient() {
    Customer returned = new Customer(UUID.randomUUID().toString(),
        Faker.instance().name().fullName());
    when(clientRepository.save(any())).thenReturn(returned);

    StepVerifier.create(service.createRandomCustomer()).expectNext(returned).verifyComplete();
  }

  @Test
  @DisplayName("Should list customers")
  void shouldListAllCustomers() {
    Customer customer1 = new Customer(UUID.randomUUID().toString(),
        Faker.instance().name().fullName());
    Customer customer2 = new Customer(UUID.randomUUID().toString(),
        Faker.instance().name().fullName());
    Customer customer3 = new Customer(UUID.randomUUID().toString(),
        Faker.instance().name().fullName());
    int page = 0;
    int pageSize = 5;
    when(clientRepository.findAll(PageRequest.of(page, pageSize))).thenReturn(
        List.of(customer1, customer2, customer3));

    StepVerifier.create(service.getAllCustomers(page, pageSize))
        .expectNext(customer1, customer2, customer3)
        .verifyComplete();
  }

  @Test
  @DisplayName("Should get customer")
  void shouldGetCustomer() {
    String customerId = UUID.randomUUID().toString();
    Customer customer1 = new Customer(customerId,
        Faker.instance().name().fullName());

    when(clientRepository.findById(customerId)).thenReturn(
        Optional.of(customer1));

    StepVerifier.create(service.getCustomer(customerId))
        .expectNext(customer1)
        .verifyComplete();

    StepVerifier.create(service.getCustomer("customerId"))
        .verifyComplete();
  }

  @Test
  @DisplayName("Should throw an error when trying to get or create customer")
  void shouldThrowExceptionGetOrCreateCustomer() {
    String customerId = UUID.randomUUID().toString();
    String customerName = Faker.instance().name().fullName();

    StepVerifier.create(service.getOrCreateCustomer(customerId, customerName))
        .expectError(CustomerDoesNotExistException.class)
        .verify();
  }

  @Test
  @DisplayName("Should create new customer if flag is set")
  void shouldGetOrCreateCustomer() {
    String customerId = UUID.randomUUID().toString();
    String customerName = Faker.instance().name().fullName();
    Customer customer = new Customer(customerId,
        customerName);

    service.autoCreateCustomer = true;

    when(clientRepository.save(customer))
        .thenReturn(customer);

    StepVerifier.create(service.getOrCreateCustomer(customerId, customerName))
        .expectNext(customer).verifyComplete();
  }

  @Test
  @DisplayName("Should calculate customer info - downgrade")
  void shouldCalculateCustomerInfoDowngrade() {
    String customerId = UUID.randomUUID().toString();
    int currentYear = ZonedDateTime.now().getYear();
    int lastYear = currentYear - 1;
    ZonedDateTime beginningOfLastYear = ZonedDateTime.of(lastYear, 1, 1, 0, 0, 0, 0,
        ZoneId.systemDefault());
    ZonedDateTime endOfThisYear = ZonedDateTime.of(currentYear, 12, 30, 23, 59, 59, 0,
        ZoneId.systemDefault());
    Customer customer = new Customer(customerId, Faker.instance().name().fullName());

    when(clientRepository.findById(customerId)).thenReturn(Optional.of(customer));
    int sumForCurrentYear = 3000;
    int sumForLastYear = 15000;
    when(orderRepository.sumOrderTotalsForYear(customer, currentYear)).thenReturn(
        sumForCurrentYear);
    when(orderRepository.sumOrderTotalsForYear(customer, lastYear)).thenReturn(sumForLastYear);
    service.tierValues = new int[]{2000, 10000, 15000};
    service.timezone = ZoneId.systemDefault().getId();

    CustomerInfo customerInfo = new CustomerInfo(3, beginningOfLastYear,
        sumForLastYear + sumForCurrentYear, 0, 1,
        endOfThisYear, service.tierValues[2] - sumForCurrentYear);

    StepVerifier.create(service.getCustomerInfo(customerId))
        .expectNext(customerInfo)
        .verifyComplete();
  }

  @Test
  @DisplayName("Should calculate customer info - no downgrade")
  void shouldCalculateCustomerInfoNoDowngrade() {
    String customerId = UUID.randomUUID().toString();
    int currentYear = ZonedDateTime.now().getYear();
    int lastYear = currentYear - 1;
    ZonedDateTime beginningOfLastYear = ZonedDateTime.of(lastYear, 1, 1, 0, 0, 0, 0,
        ZoneId.systemDefault());
    ZonedDateTime endOfThisYear = ZonedDateTime.of(currentYear, 12, 30, 23, 59, 59, 0,
        ZoneId.systemDefault());
    Customer customer = new Customer(customerId, Faker.instance().name().fullName());

    when(clientRepository.findById(customerId)).thenReturn(Optional.of(customer));
    int sumForCurrentYear = 15300;
    int sumForLastYear = 15000;
    when(orderRepository.sumOrderTotalsForYear(customer, currentYear)).thenReturn(
        sumForCurrentYear);
    when(orderRepository.sumOrderTotalsForYear(customer, lastYear)).thenReturn(sumForLastYear);
    service.tierValues = new int[]{2000, 10000, 15000};
    service.timezone = ZoneId.systemDefault().getId();

    CustomerInfo customerInfo = new CustomerInfo(3, beginningOfLastYear,
        sumForLastYear + sumForCurrentYear, 0, null,
        endOfThisYear, 0);

    StepVerifier.create(service.getCustomerInfo(customerId))
        .expectNext(customerInfo)
        .verifyComplete();
  }

  @Test
  @DisplayName("Should calculate customer info - for customer without orders")
  void shouldCalculateCustomerInfoNoOrders() {
    String customerId = UUID.randomUUID().toString();
    int currentYear = ZonedDateTime.now().getYear();
    int lastYear = currentYear - 1;
    ZonedDateTime beginningOfLastYear = ZonedDateTime.of(lastYear, 1, 1, 0, 0, 0, 0,
        ZoneId.systemDefault());
    ZonedDateTime endOfThisYear = ZonedDateTime.of(currentYear, 12, 30, 23, 59, 59, 0,
        ZoneId.systemDefault());
    Customer customer = new Customer(customerId, Faker.instance().name().fullName());

    when(clientRepository.findById(customerId)).thenReturn(Optional.of(customer));
    int sumForCurrentYear = 0;
    int sumForLastYear = 0;
    when(orderRepository.sumOrderTotalsForYear(customer, currentYear)).thenReturn(
        sumForCurrentYear);
    when(orderRepository.sumOrderTotalsForYear(customer, lastYear)).thenReturn(sumForLastYear);
    service.tierValues = new int[]{2000, 10000, 15000};
    service.timezone = ZoneId.systemDefault().getId();

    CustomerInfo customerInfo = new CustomerInfo(0, beginningOfLastYear,
        0, 2000, null,
        endOfThisYear, 0);

    StepVerifier.create(service.getCustomerInfo(customerId))
        .expectNext(customerInfo)
        .verifyComplete();
  }
}