package pl.orzechsoft.tiercalculator.system;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import pl.orzechsoft.tiercalculator.model.customer.Customer;
import pl.orzechsoft.tiercalculator.model.customer.CustomerInfo;
import pl.orzechsoft.tiercalculator.model.order.OrderReport;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class SystemTest {

  @Value("${server.port}")
  private int serverPort;

  @Test
  @DisplayName("Task flow test - executes the usual flow")
  void taskFlowTest() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    WebClient webClient = WebClient.create("http://localhost:" + serverPort);
    List<Customer> customers = webClient.post()
        .uri("/admin/create-customer")
        .header(AUTHORIZATION, "Bearer " + createAndSignJwt("admin", List.of("admin")))
        .exchangeToMono(getClientResponseHandler(Customer.class))
        .repeat(2)
        .collectList()
        .block();
    assert customers != null;
    String token1 = createAndSignJwt(customers.get(0).getId(),
        List.of("customer:read", "order:report"));
    String token2 = createAndSignJwt(customers.get(1).getId(),
        List.of("customer:read", "order:report"));

    int currentYear = ZonedDateTime.now().getYear();

    // Make sure that the first tier is 0
    StepVerifier.create(getCustomerInfo(webClient, customers.get(0).getId(), token1))
        .expectNextMatches(
            customerInfo -> customerInfo.getCurrentTier() == 0 && customerInfo.getTotalSpent() == 0)
        .verifyComplete();

    // Report order from 2 years ago and make sure it doesn't affect the tier
    StepVerifier.create(reportOrder(webClient, token1,
        getOrderReport(customers.get(0), 100, currentYear - 2))).verifyComplete();
    StepVerifier.create(getCustomerInfo(webClient, customers.get(0).getId(), token1))
        .expectNextMatches(
            customerInfo -> customerInfo.getCurrentTier() == 0 && customerInfo.getTotalSpent() == 0)
        .verifyComplete();

    // Report order for last year and make sure it is included in the tier calculation
    StepVerifier.create(reportOrder(webClient, token1,
        getOrderReport(customers.get(0), 21000, currentYear - 1))).verifyComplete();
    StepVerifier.create(getCustomerInfo(webClient, customers.get(0).getId(), token1))
        .expectNextMatches(
            customerInfo -> customerInfo.getCurrentTier() == 1
                && customerInfo.getTotalSpent() == 21000
                && customerInfo.getDowngradeTier() == 0)
        .verifyComplete();

    // Report order for this year and make sure it is included in the tier calculation
    StepVerifier.create(reportOrder(webClient, token1,
        getOrderReport(customers.get(0), 21000, currentYear))).verifyComplete();
    StepVerifier.create(getCustomerInfo(webClient, customers.get(0).getId(), token1))
        .expectNextMatches(
            customerInfo -> customerInfo.getCurrentTier() == 1
                && customerInfo.getTotalSpent() == 42000
                && customerInfo.getDowngradeTier() == null
                && customerInfo.getToSpendToReachNextTier() == 8000)
        .verifyComplete();

    // Report order for this year for another customer and check if it's in the top tier
    StepVerifier.create(reportOrder(webClient, token2,
        getOrderReport(customers.get(1), 1221000, currentYear))).verifyComplete();
    StepVerifier.create(getCustomerInfo(webClient, customers.get(1).getId(), token2))
        .expectNextMatches(
            customerInfo -> customerInfo.getCurrentTier() == 3
                && customerInfo.getTotalSpent() == 1221000
                && customerInfo.getDowngradeTier() == null
                && customerInfo.getToSpendToReachNextTier() == 0)
        .verifyComplete();

    // Report another order for this year and make sure the customer has jumped by one tier
    StepVerifier.create(reportOrder(webClient, token1,
        getOrderReport(customers.get(0), 30000, currentYear))).verifyComplete();
    StepVerifier.create(getCustomerInfo(webClient, customers.get(0).getId(), token1))
        .expectNextMatches(
            customerInfo -> customerInfo.getCurrentTier() == 2
                && customerInfo.getTotalSpent() == 72000
                && customerInfo.getDowngradeTier() == null
                && customerInfo.getToSpendToReachNextTier() == 28000)
        .verifyComplete();
  }

  private OrderReport getOrderReport(Customer customer, int amount, int year) {
    return new OrderReport(UUID.randomUUID().toString(),
        customer.getId(),
        customer.getName(),
        amount,
        ZonedDateTime.of(year, 1, 1, 1, 0, 0, 0, ZoneId.systemDefault()));
  }

  private Mono<Void> reportOrder(WebClient webClient, String token, OrderReport report) {
    return webClient.post()
        .uri("/order")
        .header(AUTHORIZATION, "Bearer " + token)
        .body(BodyInserters.fromValue(report))
        .exchangeToMono(getClientResponseHandler(null));
  }

  private Mono<CustomerInfo> getCustomerInfo(WebClient webClient, String customerId, String token) {
    return webClient.get()
        .uri("/customer/{id}/info", customerId)
        .header(AUTHORIZATION, "Bearer " + token)
        .exchangeToMono(getClientResponseHandler(CustomerInfo.class));
  }

  private <T> Function<ClientResponse, Mono<T>> getClientResponseHandler(Class<T> clazz) {
    return response -> {
      if (response.statusCode().equals(HttpStatus.OK)) {
        return clazz == null ? Mono.empty() : response.bodyToMono(clazz);
      } else {
        response.bodyToMono(List.class).publishOn(Schedulers.parallel());
        return response.createException().flatMap(Mono::error);
      }
    };
  }

  private String createAndSignJwt(String subject, List<String> scopes)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    return createAndSignJwt(subject, scopes, null);
  }

  private String createAndSignJwt(String subject, List<String> scopes, String role)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
    JwtBuilder builder = Jwts.builder()
        .claim("sub", subject)
        .claim("scope", scopes)
        .claim("iat", System.currentTimeMillis());
    builder
        .signWith(signatureAlgorithm, getPrivateKey());
    return builder.compact();
  }

  private RSAPrivateKey getPrivateKey()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    String key = new String(this.getClass().getClassLoader().getResourceAsStream("private_key.rsa")
        .readAllBytes());
    String privateKeyPEM = key
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replaceAll(System.lineSeparator(), "")
        .replace("-----END PRIVATE KEY-----", "");
    byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encoded);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return (RSAPrivateKey) keyFactory.generatePrivate(spec);
  }
}
