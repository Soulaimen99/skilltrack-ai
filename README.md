# SkillTrack AI

SkillTrack AI is a backend service that helps individuals track their daily learning progress and generates AI-powered summaries of what they’ve learned.

## 🚀 Tech Stack

- **Java 21**
- **Quarkus** (RESTEasy, Panache, OpenAPI)
- **PostgreSQL** (Docker-based)
- **OpenAPI Generator**
- **OpenAI GPT API**
- **Swagger UI**

---

## 🛠 Setup Instructions

### 1. Clone the project

```bash
git clone https://github.com/Soulaimen99/skilltrack-ai.git
cd skilltrack-ai
```

---

### 2. Start PostgreSQL (via Docker)

```bash
docker-compose up -d
```

Default credentials:
- Host: `localhost:5432`
- User: `skilluser`
- Pass: `skillpass`
- DB: `skilltrack`

---

### 3. Configure the app

Edit `src/main/resources/application.yml`:

```yaml
quarkus:
  datasource:
    db-kind: postgresql
    username: skilluser
    password: skillpass
    jdbc:
      url: jdbc:postgresql://localhost:5432/skilltrack
  hibernate-orm:
    database:
      generation: drop-and-create
    log:
      sql: true

  swagger-ui:
    always-include: true
    path: /swagger

openai:
  api:
    key: YOUR_OPENAI_API_KEY
    url: https://api.openai.com/v1/chat/completions
```

---

### 4. Run the app in dev mode

```bash
./mvnw quarkus:dev
```

Open in browser:

- Swagger UI → `http://localhost:8080/q/swagger-ui`

---

### 5. Generate API code (if you update `openapi.yml`)

```bash
java -jar openapi-generator-cli.jar generate \
  -i src/main/openapi/openapi.yml \
  -g java \
  -o target/generated-sources/openapi \
  --api-package com.skilltrack.api \
  --model-package com.skilltrack.model \
  --additional-properties=useTags=true
```

Then copy necessary files into `src/main/java`.

---

## ✅ MVP Features

- [x] Add learning logs
- [x] View log history
- [x] Generate AI-powered summaries
- [ ] Tags, flashcards (coming soon)

---

## 📦 Future Additions

- PWA mobile app
- Markdown export
- AI goal suggestions
- GitHub activity sync

---

## 📜 License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

---

Built with ☕, 💡, and a lot of 🚀
