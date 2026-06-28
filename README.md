# LIBRO - Spring Boot Backend API

This is the backend REST API service for **LIBRO (BookHive)**, a premium Library Management System. Built with Spring Boot and Java 21, this service handles user authentication, catalog management, book lending, reservation queues, fine tracking, subscription plan billing, and secure checkout payments.

---

## 🛠️ Tech Stack & Dependencies

- **Platform & Framework**: Java 21, Spring Boot 4.0.x / 3.x
- **Data Access & Database**: Spring Data JPA, Hibernate, MySQL Connector J
- **Security & Identity**: Spring Security, JWT (JSON Web Tokens via JJWT `0.12.5`), Spring Security OAuth2 Client (for Google Login)
- **Integrations**: Razorpay Java SDK (`1.4.8`) for payment processing, Spring Boot Starter Mail for transactional email alerts (password reset links)
- **Developer Utilities**: Lombok, Spring Boot Validation Starter, Jackson JSON Mapper

---

## 📁 Package & Directory Architecture

```
backend/src/main/java/LIBRO/libro/
├── Config/             # Security configs, OAuth2 configuration, CORS settings
├── Controllers/        # REST controllers exposing all endpoints (Admin, Auth, Loans, Fines, etc.)
├── Domain/             # Global enums and models (e.g., LoanStatus, UserRole, AuthProvider)
├── Entities/           # JPA entities (User, Book, BookLoan, Reservation, Subscription, Fine, Payment)
├── Exceptions/         # Custom runtime exceptions and error handling wrappers
├── Mapper/             # DTO mapping classes for translating entities to payloads
├── Payload/            # Request and Response DTO payloads
├── Repositeries/       # Spring Data JPA Repository interfaces
├── Service/            # Business logic interfaces & implementation classes
└── event/              # Spring Event publishers and listeners (e.g., PaymentEventListener)
```

---

## ⚙️ Installation & Configuration

### Prerequisites
- **Java Development Kit (JDK)**: Version 21
- **Database**: MySQL Server 8.0+

### 1. Database Setup
Create a MySQL database named `libro_db`:
```sql
CREATE DATABASE libro_db;
```

### 2. Application Properties Configuration
Create/Update `backend/src/main/resources/application.properties` with your credentials:

```properties
spring.application.name=libro
server.port=8080

# JPA & DDL Auto
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Database Credentials
spring.datasource.url=jdbc:mysql://localhost:3306/libro_db
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

# JWT Secret Configuration
libro.jwt.secret=fefgwoiefjfijrj2eiewfnegnngfkwfw... # Must be 256-bit secure key

# OAuth2 Google Credentials
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=email,profile

# Transactional Mail Config
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_SMTP_EMAIL
spring.mail.password=YOUR_SMTP_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Razorpay Keys
razorpay.key.id=YOUR_RAZORPAY_KEY
razorpay.key.secret=YOUR_RAZORPAY_SECRET
razorpay.callback.base-url=http://localhost:5173
```

---

## 🚀 Running the Application

Ensure you are in the `backend` directory:

1. **Make Maven Wrapper executable (Unix/macOS)**:
   ```bash
   chmod +x mvnw
   ```

2. **Run the Spring Boot application**:
   ```bash
   ./mvnw spring-boot:run
   ```
   The backend server will bootstrap and run on [http://localhost:8080](http://localhost:8080).

---

## 🔒 Entity Relations & DB Integrity
All major entities are designed to cascade user deletion to avoid runtime exceptions and keep database state consistent:
- `BookReview`, `BookLoan`, `Reservation`, `Subscription`, `Fine`, `Payment`, and `Wishlist` relate to the `User` entity via `@ManyToOne` association.
- Each association is configured with Hibernate `@OnDelete(action = OnDeleteAction.CASCADE)` rules, ensuring that removing a library user automatically purges all their history, loans, unpaid fines, and reviews in the database.
