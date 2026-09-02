# Multiboard API

A test-driven mini project for a multi-board API built using Spring Boot and JPA.

## 🛠 Tech Stack
- **Framework:** Spring Boot
- **Data Access:** Spring Data JPA(with Auditing), Querydsl
- **Security & Auth:** Spring Security, JWT (jjwt)
- **Database & Cache:** H2 Database, Redis (Token Management)
- **Development Methodology:** TDD (Test-Driven Development)

## 📌 Features
- Multi-board management and API implementation
- Dynamic query handling with Querydsl
- Two-Track JWT Authentication System:
  - **Access Token Blacklist:** Secure logout handling via Redis TTL
  - **Refresh Token Whitelist:** Token reissue and session extension management
- Robust testing environment configured with H2, Redis, and JPA Auditing

## 📈 Changelog & Project Guide

📅 [2026-09-02] - feature/jwt-refresh-token-and-auth-flow
- Implement two-track JWT authentication using Redis (Access Token blacklist & Refresh Token whitelist)
- Add /api/auth/reissue endpoint in AuthController for Access Token renewal using Refresh Token
- Update AuthService logout logic to securely delete the user's Refresh Token from Redis to prevent token hijacking
- Implement AuthFlowIntegrationTest to verify the complete authentication lifecycle (Login -> API access -> Reissue -> Logout -> Block)

### 📅 [2026-08-30] - feature/jpa-auditing-and-db-profiles
- Separate Database configurations by profile (In-memory DB with `create-drop` for TDD, File-based H2 with `update` for Dev)
- Resolve H2 Web Console lock issues by enabling `AUTO_SERVER=TRUE` for concurrent connections

### 📅 [2026-08-29] - feature/spring-security-jwt
- Apply Spring Security with Stateless (Session disabled) configuration for REST API
- Implement JWT-based authentication (`JwtTokenProvider`, `JwtFilter`)
- Handle Security Exceptions (401 Unauthorized, 403 Forbidden) integrated with i18n `ApiResponse`
- Write Unit & Integration tests for Security and JWT using `MockMvc`

### 📅 [2026-08-28] - feature/env-setup
- Profile Separation (`dev`, `stage`, `real`)
- Internationalization (`i18n`) Message File Setup
      


- Implement full JPA Auditing (`@CreatedBy`, `@LastModifiedBy`) integrated with Spring Security's `SecurityContextHolder` via `AuditorAwareImpl`
- Extract Auditing configuration into a dedicated `JpaAuditingConfig` class
