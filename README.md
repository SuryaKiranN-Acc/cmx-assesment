# cmx-assesment
cmx assesment

This service implements the Claims Platform backend MVP (modular monolith). It is API-first and uses OpenAPI-generated interfaces.

Run locally:

```bash
mvn clean package
mvn spring-boot:run
```

H2 console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:claimsdb`, user `sa`)

Key points:
- monolith with `claim`, `workflow`.
- State machine enforces claim lifecycle transitions.
- Events are published via `DomainEventPublisher` (backed by Spring Application Events). Can be swapped for Kafka later.
- Minimal in-memory H2 DB for demo.
