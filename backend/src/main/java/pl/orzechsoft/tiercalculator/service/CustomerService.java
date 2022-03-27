package pl.orzechsoft.tiercalculator.service;

import com.github.javafaker.Faker;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.orzechsoft.tiercalculator.model.customer.Customer;
import pl.orzechsoft.tiercalculator.model.customer.CustomerInfo;
import pl.orzechsoft.tiercalculator.model.customer.GetCustomersResponse;
import pl.orzechsoft.tiercalculator.model.exception.CustomerDoesNotExistException;
import pl.orzechsoft.tiercalculator.repository.CustomerRepository;
import pl.orzechsoft.tiercalculator.repository.OrderRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;
  private final ClientMessageService messageService;

  @Value("${tier.values}")
  int[] tierValues;

  @Value("${server.timezone}")
  String timezone;

  @Value("${customer.auto-create}")
  boolean autoCreateCustomer;

  public Mono<Customer> createRandomCustomer() {
    return Mono.just(customerRepository.save(
        new Customer(UUID.randomUUID().toString(), Faker.instance().name().fullName())));
  }

  public Mono<GetCustomersResponse> getAllCustomers(int page, int size) {
    return Mono.just(customerRepository.findAll(PageRequest.of(page, size)))
        .zipWith(Mono.just(customerRepository.count()))
        .map(customersAndCount -> {
          System.out.println(customersAndCount);
          return new GetCustomersResponse(customersAndCount.getT1(),
              Math.toIntExact(customersAndCount.getT2()));
        });
  }

  public Mono<Customer> getCustomer(String id) {
    return Mono.justOrEmpty(customerRepository.findById(id));
  }

  public Mono<Customer> getOrCreateCustomer(String id, String customerName) {
    return getCustomer(id)
        .switchIfEmpty(autoCreateCustomer ?
            Mono.just(customerRepository.save(new Customer(id, customerName))) :
            Mono.error(new CustomerDoesNotExistException(
                messageService.getMessage("customer.does.not.exist", id))));
  }

  public Mono<CustomerInfo> getCustomerInfo(String id) {
    return getCustomer(id).switchIfEmpty(Mono.error(new CustomerDoesNotExistException(
            messageService.getMessage("customer.does.not.exist", id))))
        .map(this::calculateCustomerInfo);
  }

  private CustomerInfo calculateCustomerInfo(Customer customer) {
    ZoneId serverTimezone = ZoneId.of(timezone);
    int currentYear = ZonedDateTime.now(serverTimezone).getYear();
    int lastYear = currentYear - 1;
    int sumForCurrentYear = getSumOrderTotalsForYear(customer, currentYear);
    int sumForLastYear = getSumOrderTotalsForYear(customer, lastYear);
    int totalSum = sumForLastYear + sumForCurrentYear;
    int currentTier = calculateTier(totalSum);
    ZonedDateTime beginningOfLastYear = ZonedDateTime.of(lastYear, 1, 1, 0, 0, 0, 0,
        serverTimezone);
    ZonedDateTime endOfThisYear = ZonedDateTime.of(currentYear, 12, 30, 23, 59, 59, 0,
        serverTimezone);
    Integer downgradeTier = calculateDowngradeTier(sumForCurrentYear, currentTier);
    return new CustomerInfo(customer.getName(),
        currentTier,
        beginningOfLastYear,
        totalSum,
        calculateAmountToSpend(currentTier + 1, totalSum),
        downgradeTier,
        endOfThisYear,
        downgradeTier == null ? 0 : calculateAmountToSpend(currentTier, sumForCurrentYear));
  }

  private Integer calculateDowngradeTier(int sumForCurrentYear, int currentTier) {
    int newTier = calculateTier(sumForCurrentYear);
    return newTier == currentTier ? null : newTier;
  }

  private int calculateAmountToSpend(int currentTier, int totalSum) {
    return currentTier > tierValues.length ? 0
        : Math.max(tierValues[Math.max(currentTier - 1, 0)] - totalSum, 0);
  }

  private int calculateTier(int orderTotal) {
    for (int i = 0; i < tierValues.length; i++) {
      if (orderTotal < tierValues[i]) {
        return i;
      }
    }
    return tierValues.length;
  }

  private int getSumOrderTotalsForYear(Customer customer, int currentYear) {
    Integer total = orderRepository.sumOrderTotalsForYear(customer, currentYear);
    return total == null ? 0 : total;
  }
}
