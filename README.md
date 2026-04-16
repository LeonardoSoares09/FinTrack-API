# FinTrack API 💰

API REST para controle de finanças pessoais. Permite registrar receitas e despesas, categorizá-las e consultar resumos financeiros por período.

## Tecnologias

- **Java 21** + **Spring Boot 4**
- **Spring Security** + **JWT** (JJWT 0.12.6)
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL**
- **Docker** + **Docker Compose**
- **JUnit 5** + **Mockito**
- **Lombok**
- **Maven**

## Como rodar

### Pré-requisitos
- Docker e Docker Compose instalados

### Subindo o projeto

```bash
git clone https://github.com/LeonardoSoares09/FinTrack-API.git
cd FinTrack-API
docker compose up --build
```

A aplicação estará disponível em `http://localhost:8080`.

> As credenciais do banco e a chave JWT já estão configuradas no `docker-compose.yml` para facilitar a execução local.

---

## Autenticação

A API usa autenticação via **JWT Bearer Token**.

Após registrar e fazer login, inclua o token em todas as requisições protegidas:

```
Authorization: Bearer <seu-token>
```

---

## Endpoints

### Auth (público)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/register` | Registra um novo usuário |
| POST | `/auth/login` | Realiza login e retorna o JWT |

**Registro:**
```json
POST /auth/register
{
  "name": "Leonardo",
  "email": "leonardo@email.com",
  "password": "123456"
}
```

**Login:**
```json
POST /auth/login
{
  "email": "leonardo@email.com",
  "password": "123456"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### Categorias 🔒

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/categories` | Cria uma categoria |
| GET | `/categories` | Lista todas as categorias |
| GET | `/categories/{id}` | Busca categoria por ID |
| PUT | `/categories/{id}` | Atualiza uma categoria |
| DELETE | `/categories/{id}` | Remove uma categoria |

**Exemplo de criação:**
```json
POST /categories
{
  "name": "Alimentação",
  "description": "Gastos com comida e mercado"
}
```

---

### Transações 🔒

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/transactions` | Registra uma transação |
| GET | `/transactions` | Lista transações do usuário (com filtros e paginação) |
| GET | `/transactions/{id}` | Busca transação por ID |
| PUT | `/transactions/{id}` | Atualiza uma transação |
| DELETE | `/transactions/{id}` | Remove uma transação |
| GET | `/transactions/summary` | Resumo financeiro do usuário |

**Exemplo de criação:**
```json
POST /transactions
{
  "amount": 1500.00,
  "type": "RECEITA",
  "date": "2026-04-15",
  "description": "Salário",
  "categoryId": "uuid-da-categoria"
}
```

> `type` aceita: `RECEITA` ou `DESPESA`

**Filtros disponíveis:**
```
GET /transactions?type=DESPESA
GET /transactions?categoryId={uuid}
GET /transactions?type=RECEITA&categoryId={uuid}
GET /transactions?page=0&size=10
```

**Resumo financeiro:**
```json
GET /transactions/summary

{
  "totalReceita": 5000.00,
  "totalDespesas": 1200.00,
  "saldo": 3800.00
}
```

---

## Estrutura do projeto

```
src/main/java/com/leonardosoares/fintrack_api/
├── config/
│   ├── exception/       # GlobalExceptionHandler e exceções customizadas
│   └── security/        # JwtFilter, SecurityConfig, UserDetailsServiceImpl
├── controller/
│   └── dto/             # DTOs de request e response
├── mapper/              # Mapeamento entre entidades e DTOs
├── model/
│   └── enums/           # TransactionType
├── repository/          # Interfaces JPA
├── service/             # Lógica de negócio
└── spec/                # Specifications para filtros dinâmicos
```

---

## Variáveis de ambiente

| Variável | Descrição |
|----------|-----------|
| `SPRING_DATASOURCE_URL` | URL de conexão com o PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Chave secreta para assinar os tokens (Base64) |
| `JWT_EXPIRATION` | Tempo de expiração do token em ms (ex: 86400000 = 24h) |

---

## Testes

```bash
./mvnw test
```

Cobertura de testes unitários com JUnit 5 e Mockito para `CategoryService` e `TransactionService`.