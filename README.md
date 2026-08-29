# Multiboard API

A test-driven mini project for a multi-board API built using Spring Boot and JPA.

## 🛠 Tech Stack
- **Framework:** Spring Boot
- **Data Access:** Spring Data JPA, Querydsl
- **Security & Auth:** Spring Security, JWT (jjwt)
- **Database:** H2 Database
- **Development Methodology:** TDD (Test-Driven Development)

## 📌 Features
- Multi-board management and API implementation
- Dynamic query handling with Querydsl
- Robust testing environment configured with H2 and JPA

---

## 📈 Changelog & Project Guide

### 📅 [2026-08-28] - feature/env-setup
- Profile Separation (`dev`, `stage`, `real`)
- Internationalization (`i18n`) Message File Setup
      
### 📅 [2026-08-29] - feature/spring-security-jwt

- Apply Spring Security with Stateless (Session disabled) configuration for REST API
- Implement JWT-based authentication (`JwtTokenProvider`, `JwtFilter`)
- Handle Security Exceptions (401 Unauthorized, 403 Forbidden) integrated with i18n `ApiResponse`
- Write Unit & Integration tests for Security and JWT using `MockMvc`
