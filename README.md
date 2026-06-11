# HotelSystem

Sistema de gerenciamento de reservas de hotel com backend em Spring Boot e frontend em React.

> Projeto desenvolvido para a disciplina de **Verificação, Validação e Teste de Software (VVTS)** e reutilizado na disciplina de **Gestão e Configuração de Software (GCSW)**, onde foi aplicada a infraestrutura de conteinerização com Docker Compose.

## Estrutura do projeto

```
HotelSystem/
├── src/                          → código-fonte do backend (Spring Boot)
├── frontend/                     → interface web (React + Vite)
├── Dockerfile                    → imagem do backend
├── docker-compose.yml            → orquestração dos serviços
├── .env.example                  → variáveis de ambiente necessárias
└── pom.xml
```

---

## Rodar com Docker (recomendado)

### Pré-requisitos

- Docker e Docker Compose instalados

### Passos

```bash
cp .env.example .env
# edite o .env com os valores desejados
docker compose up --build
```

O sistema sobe com três serviços:

| Serviço | Descrição | Porta |
|---------|-----------|-------|
| `db` | Banco de dados PostgreSQL | 5432 |
| `backend` | API REST (Spring Boot) | 8080 |
| `frontend` | Interface web (React + Nginx) | 80 |

Acesse o sistema em `http://localhost`.

O banco já sobe com dados mockados via Flyway. Use as credenciais abaixo para acessar:

```
Email: admin@hotel.com
Senha: admin123
```

---

## Rodar localmente (sem Docker)

### Pré-requisitos

- Java 21
- Maven
- Node.js 18+ e npm
- PostgreSQL rodando localmente

### Backend

Configure as variáveis de ambiente ou crie um banco com as credenciais padrão do `application.properties`:

```
banco: hoteldb
usuário: hotel
senha: hotel123
```

```bash
mvn spring-boot:run
```

O servidor sobe na porta `8080`. O Flyway executa automaticamente a migration `V1_mock_data.sql`, criando as tabelas e populando o banco com dados de exemplo.

### Testes

```bash
mvn test
```

### Frontend

```bash
cd frontend
cp .env.example .env.local
npm install
npm run dev
```

Acesse em `http://localhost:5173`.

---

## Endpoints

Todos os endpoints exigem autenticação via JWT, exceto os de registro e login.

O token deve ser enviado no header:
```
Authorization: Bearer <token>
```

#### Autenticação

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/v1/register` | Cadastrar novo usuário |
| POST | `/api/v1/authenticate` | Login — retorna o token JWT |

Corpo do registro:
```json
{
  "name": "João",
  "lastname": "Silva",
  "email": "joao@email.com",
  "password": "senha123"
}
```

Corpo do login:
```json
{
  "username": "joao@email.com",
  "password": "senha123"
}
```

#### Hóspedes

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/v1/guests` | Listar hóspedes |
| POST | `/api/v1/guests` | Cadastrar hóspede |

#### Reservas

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/v1/bookings` | Listar todas as reservas |
| GET | `/api/v1/bookings/{id}` | Buscar reserva por ID |
| POST | `/api/v1/bookings` | Criar reserva |
| PUT | `/api/v1/bookings/{id}` | Atualizar reserva |
| PATCH | `/api/v1/bookings/{id}/cancel` | Cancelar reserva |
| PATCH | `/api/v1/bookings/{id}/checkin` | Realizar check-in |
| PATCH | `/api/v1/bookings/{id}/checkout` | Realizar check-out |

Corpo para criar reserva:
```json
{
  "guestId": "uuid-do-hospede",
  "roomCategory": "STANDARD",
  "checkIn": "2026-05-20",
  "checkOut": "2026-05-25"
}
```

Categorias disponíveis: `STANDARD`, `DELUXE`, `SUITE`.

### Documentação interativa (Swagger)

Com o backend rodando, acesse:
```
http://localhost:8080/api/v1/api-docs
```
