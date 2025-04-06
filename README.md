# 🚀 SkillTrack AI

A Quarkus-based backend for tracking daily learning activities, summarizing them using AI, and managing logs via a REST API.

---

## 🔧 Tech Stack

- [Quarkus 3.21.0](https://quarkus.io/)
- Java 21
- REST (Jakarta)
- PostgreSQL (via Docker)
- Hibernate ORM (Panache)
- OpenAPI + Swagger UI
- OpenAPI Generator (interface-only, jakarta-based)
- JUnit 5 + RestAssured
- Lombok for clean models

---

## 📦 Features

- Add and retrieve learning logs
- Summarize logs using AI (OpenAI-ready)
- API documented via Swagger UI
- Clean and modern Quarkus setup with zero `javax.*`
- Easily extendable and testable

---

## 🧩 Setup

### 1. Clone & Build

```bash
git clone https://github.com/yourusername/skilltrack-ai.git
cd skilltrack-ai
./mvnw clean compile
```

### 2. Start PostgreSQL via Docker

```bash
docker-compose up -d
```

Default DB credentials:

- DB: `skilltrack`
- User: `skilluser`
- Password: ``

### 3. Run the App

```bash
./mvnw quarkus:dev
```

Dev UI available at: [http://localhost:8080/q/dev-ui](http://localhost:8080/q/dev-ui)

---

## 🌐 API Docs

- Swagger UI: [http://localhost:8080/swagger](http://localhost:8080/swagger)
- OpenAPI JSON: [http://localhost:8080/q/openapi](http://localhost:8080/q/openapi)

---

## 🧪 Running Tests

```bash
./mvnw test
```

Covers:

- Log creation and retrieval
- Summary endpoint
- Model equality + validation

---

## 📜 OpenAPI Spec

The file `src/main/openapi/openapi.yml` defines your API and is used to generate DTOs and interfaces using:

```bash
./mvnw compile
```

No need to run the generator manually — it's integrated with the Maven build.

---

## 🧠 Notes

- OpenAPI code is generated to `target/generated-sources/openapi`
- Models use Lombok (`@Data`) to simplify getters, setters, equals/hashCode
- Hibernate auto-creates tables using `drop-and-create` mode
- Ready to integrate OpenAI client with your API key

---

## 📌 License

MIT © 2025 Soulaimen Choura
