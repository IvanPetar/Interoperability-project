# Interoperability Project (IIS Backend)

A university project for the *Interoperability of Information Systems* course at Algebra Bernays University. It shows how one product catalog can be exposed and consumed through several API styles and data formats: **REST, SOAP, GraphQL and gRPC**, with **XML/JSON validation** and **JWT authentication**.

## Features

- **REST API**: CRUD for products, with role-based access (`USER` / `ADMIN`)
- **SOAP web service**: product search with a WSDL generated from an XSD, plus XPath/JAXB processing
- **GraphQL**: queries and mutations for products, with a GraphiQL playground
- **gRPC**: a weather service that reads live temperatures for Croatian cities from the DHMZ XML feed (vrijeme.hr)
- **XML & JSON validation**: uploaded files are checked against an XSD and a JSON Schema
- **JWT authentication**: register, log in, and refresh tokens (access + refresh)
- **Switchable data source**: `api.mode=custom` (local PostgreSQL via JPA/Hibernate) or `api.mode=public` (Supabase REST API)
- **Swagger / OpenAPI**: interactive API documentation
- **Frontend**: a simple HTML/JS login page and dashboard that call every API type

## Tech Stack

| Area | Technologies |
|------|--------------|
| Language | Java 21 |
| Framework | Spring Boot 4, Spring Security, Spring Data JPA |
| APIs | Spring Web MVC, Spring Web Services (SOAP), Spring GraphQL, Spring gRPC |
| Data | PostgreSQL, Supabase |
| Validation | JAXB / XSD, networknt JSON Schema Validator |
| Auth | JWT (jjwt) |
| Docs | springdoc-openapi (Swagger UI) |
| Build | Maven |
| Frontend | HTML, CSS, vanilla JavaScript |

## Project Structure

```
├── src/main/java/hr/algebra/iisbackend/
│   ├── controller/   # REST, GraphQL and weather controllers
│   ├── soap/         # SOAP endpoint
│   ├── grpc/         # gRPC weather service
│   ├── security/     # JWT filter and utilities
│   ├── service/      # Business logic + API mode router (custom / Supabase)
│   ├── validation/   # XML and JSON validators
│   ├── entity/       # JPA entities
│   ├── dto/          # Data transfer objects
│   └── config/       # Security and SOAP configuration
├── src/main/proto/        # weather.proto (gRPC contract)
├── src/main/resources/
│   ├── graphql/           # GraphQL schema
│   ├── *.xsd              # XML schemas (product, SOAP)
│   └── product-schema.json
└── iis-frontend/          # index.html (login) + dashboard.html
```

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL (or a Supabase project)

### Environment variables

| Variable | Description |
|----------|-------------|
| `DB_URL` | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/iis` |
| `DB_USERNAME` | Database user |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key used to sign JWT tokens |
| `SUPABASE_ANON_KEY` | Supabase anon key (needed only when `api.mode=public`) |

### Run

```bash
./mvnw spring-boot:run
```

On Windows: `mvnw.cmd spring-boot:run`

The frontend has no build step. Open `iis-frontend/index.html` in a browser; it expects the backend on `http://localhost:8080`.

## Endpoints

| Type | URL | Access |
|------|-----|--------|
| Auth | `POST /api/auth/register`, `/login`, `/refresh` | Public |
| REST | `GET /api/products`, `GET /api/products/{id}` | USER, ADMIN |
| REST | `POST`, `PUT`, `DELETE /api/products/**` | ADMIN |
| Validation | `GET /api/products/validate-xml`, `POST /api/products/upload` | Authenticated |
| SOAP | `POST /ws`; WSDL at `/ws/products.wsdl` | Public |
| GraphQL | `POST /graphql`; playground at `/graphiql` | Authenticated (mutations: ADMIN) |
| gRPC | `localhost:9090` (`weather.WeatherService/GetTemperature`) | Public |
| Weather (REST → gRPC) | `GET /api/weather/{city}` | Authenticated |
| Swagger UI | `/swagger-ui.html` | Public |

## Author

**Ivan Petar Šolić**
