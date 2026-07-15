# Claims Platform - Backend (MVP)

This service implements the Claims Platform backend MVP (modular monolith). It is API-first and uses OpenAPI-generated interfaces.

Run locally:

```bash
mvn clean package
mvn spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui/index.html
H2 console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:claimsdb`, user `sa`)

Key points:
- Modular monolith with `claim`, `workflow`, `dashboard`, `exposure` boundaries within one app.
- State machine enforces claim lifecycle transitions.
- Events are published via `DomainEventPublisher` (backed by Spring Application Events). Can be swapped for Kafka later.
- Minimal in-memory H2 DB for demo.
# cmx-assesment
cmx assesment
