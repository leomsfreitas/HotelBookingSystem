# 🏨 Hotel Booking System

Um sistema de gerenciamento de reservas de hotel robusto e seguro, focado em regras de negócio complexas e integridade de dados. Desenvolvido em **Java** com foco em **Domain-Driven Design (DDD)** e **Test-Driven Development (TDD)**.

## 🚀 Funcionalidades

O sistema gerencia o ciclo de vida completo de uma hospedagem, garantindo consistência em todas as etapas:

*   **Gestão de Hóspedes (Guests):** Cadastro de hóspedes com validação de unicidade de documentos (CPF).
*   **Gestão de Reservas (Bookings):** 
    *   Criação de reservas com validação de disponibilidade de quartos e bloqueio de datas retroativas.
    *   Atualização de reservas inteligente (prevenindo conflitos de datas, inclusive com a própria reserva).
*   **Ciclo de Vida da Hospedagem (Máquina de Estados):**
    *   **Check-in:** Validação temporal estrita (bloqueia check-ins antes da data agendada).
    *   **Em Andamento:** Bloqueio de alterações cadastrais enquanto o hóspede está no status `CHECKED_IN`.
    *   **Check-out & Conclusão:** O sistema exige que a reserva passe pelo check-in antes de ser concluída (`COMPLETED`), evitando furos no fluxo.
*   **Autenticação & Segurança:** Validação de payload de entrada (Bean Validation) para rotas de login e registro (`@NotBlank`, `@Email`).

## 🛠️ Tecnologias Utilizadas

Este projeto é **100% Java** e utiliza as seguintes tecnologias no ecossistema:

*   **Java 17+**
*   **Spring Boot** (Web, Data JPA, Validation)
*   **JUnit 5 & Mockito** (Testes Unitários e de Integração)
*   **Maven / Gradle** (Gerenciamento de dependências)

## 🏗️ Arquitetura

O projeto adota conceitos de **Clean Architecture** e **Domain-Driven Design (DDD)**, dividindo as responsabilidades em camadas bem definidas:

*   `domain`: Entidades ricas (`Booking`, `Guest`, `Period`), Value Objects e exceções de negócio.
*   `application` (Use Cases): Regras de orquestração (`BookingService`, `BookingUpdateService`, `GuestService`).
*   `infrastructure`: Implementações de persistência (`GuestRepository`, `BookingRepository`), integrações externas e configurações de banco de dados.
*   `presentation`: Controladores REST (APIs), DTOs de entrada/saída e validações (`AuthController`).

## ⚙️ Como Executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/leomsfreitas/HotelBookingSystem.git
   ```
2. Navegue até o diretório do projeto:
   ```bash
   cd HotelBookingSystem
   ```
3. Compile e rode os testes:
   ```bash
   ./mvnw clean test
   ```
4. Inicie a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
   *(Substitua `./mvnw` por `./gradlew` caso esteja utilizando Gradle).*

## 🧪 Testes e Qualidade (TDD)

A qualidade do software é garantida através da prática de **Test-Driven Development (TDD)**. Todas as regras de negócio críticas (como transições de estado, validações temporais e verificações de conflito) foram construídas com testes automatizados prévios (Red -> Green -> Refactor), garantindo alta cobertura e confiabilidade do domínio.
