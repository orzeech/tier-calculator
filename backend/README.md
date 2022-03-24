# Tier calculator application

This is the backend for the tier calculator application. It gathers information about the orders for
clients and calculates the information about their _tier_ for use in, for example, coupons or
benefits for higher tier customers.

## Client documentation

The documentation of the API for the client is in [openapi.yml](openapi.yml) file in the root folder
of this repository. A good idea might be to paste the contents of this file into
the [Swagger editor](https://editor.swagger.io/) and experiment a bit.

## Developer's documentation

### Running locally

Assuming that you have Java (v11 or above) and Maven installed:

#### Via a command line

To run the application locally via a command line, run `./mvnw spring-boot:run`.

#### Via an IDE

Import this repository as a maven project in your IDE and run the default Spring Boot configuration.

#### As a Docker container

Execute `mvn package` in the main directory of this repository, then build the Docker image and run
it.

### Architecture overview

This application is built using a classic Model / Service / Controller structure using the latest
stable version of Spring Boot. The main parts of the Spring library being used are:

* Spring Data JPA for communication with the database
* Spring WebFlux for nonblocking IO operations
* Spring Security for validating the JWT tokens

Spring handles many things, including the above, for the developer and is well integrated with other
libraries and software for observability, deployment, databases, etc.

### Database

This application uses an in-memory H2 database for simplicity and speed. H2 is an SQL database and
unfortunately, the R2DBC, a reactive implementation of database access is not 100% production ready
yet, so in the services I decided to "go back" to the blocking world to communicate with the DB.

Since the database connection, object mapping etc. are handled by Spring Data JPA, it can be
switched very easily to some other DB via configuration options and dependency management. No
changes in code should be necessary for other SQL databases.

### Security

All requests are required to have an `Authorization` header with a valid JWT token. This application
is supposed to work in the ecosystem of already existing microservices, so I assumed that the
clients are able to get valid tokens.

I have included a sample public (`src/main/resources/public_key.pub`) and private
key (`src/test/resources/private_key.rsa`) for token generation and verification, however it is
recommended to change the verification of the token to a more secure option. Please change this for
Phase 2. Spring Oauth2 Resource server handles verification. For more information, please visit
official [documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
.

`admin` scope is required for `/admin` endpoints, `customer:read` scope is required for getting the
information about customers, `order:report` scope is required for reporting an order. These scopes
are validated for every requrest. Please see `SecurityConfig.java` to know more.

### Tests

I believe that tests are one of the greatest ways to document code. This software at the time of
writing this documentation has over 90% code coverage, system and performance tests.

* The tests in the `controller` package test controllers, whether they return valid response codes
  and data.
* The tests in the `service` package test the main logic of the application.
* `TierCalculatorApplicationTests` runs the Spring Context.
* The tests in the `system` package are the integration tests that execute the workflow specified in
  the task and check the performance.

If something is unclear in the code, please refer to the tests that should always make it easier to
understand.

## Next steps

In order to achieve high performance and fault tolerance I suggest the following

* Replace the in-memory H2 database with a high-performance, distributed, sharded SQL database or a
  high-performance NoSQL database
* Verify the logic for validating the JWT tokens to be consistent with the company
* Run multiple instances of the application, for example as a Lambda Function or on Kubernetes (
  Dockerfile is prepared)
* Introduce caching, especially for rarely changing entities like clients
* Integrate better with the company's systems
