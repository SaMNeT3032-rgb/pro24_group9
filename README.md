# User, Role & Permission REST API

Spring Boot 4 · Java 21 · PostgreSQL · Spring Security (JWT access + refresh) · Flyway · MapStruct · Swagger

## Prerequisites
- JDK 21, Maven , PostgreSQL

## Configuration (environment variables)

| Variable | Required | Description |
|---|---|---|
| `JWT_SECRET` | yes | HMAC key, at least 32 characters (256 bits) |
| `DB_URL` | no | default `jdbc:postgresql://localhost:5432/rbac_db` |
| `DB_USERNAME` / `DB_PASSWORD` | no | default `postgres` / `postgres` |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` | no | creates a ROLE_ADMIN user on first start |
| `ADMIN_NICK_NAME` | no | default `Administrator` |

```bash
createdb pro24_group9db
export JWT_SECRET='change-me-to-a-long-random-string-of-32+chars'
export ADMIN_USERNAME=admin ADMIN_PASSWORD='Admin@12345'
mvn spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui.html

Run tests: `mvn test`

## Seeded data
- Permissions: `USER_READ`, `USER_WRITE`, `ROLE_READ`, `ROLE_WRITE`, `PERMISSION_READ`, `PERMISSION_WRITE`
- Roles: `ROLE_ADMIN` (all permissions), `ROLE_MANAGER` (user read/write, role read), `ROLE_USER` (new users get this by default)

## Endpoints

| Method | Path | Auth | Permission |
|---|---|---|---|
| POST | `/api/auth/register` | public | – |
| POST | `/api/auth/login` | public | – |
| POST | `/api/auth/refresh` | public (refresh token) | – |
| POST | `/api/auth/logout` | Bearer | – |
| GET | `/api/auth/me` | Bearer | – |
| GET/POST | `/api/users` | Bearer | `USER_READ` / `USER_WRITE` |
| GET/PUT/DELETE | `/api/users/{id}` | Bearer | `USER_READ` / `USER_WRITE` |
| GET/POST | `/api/roles` | Bearer | `ROLE_READ` / `ROLE_WRITE` |
| GET/PUT/DELETE | `/api/roles/{id}` | Bearer | `ROLE_READ` / `ROLE_WRITE` |
| GET/POST | `/api/permissions` | Bearer | `PERMISSION_READ` / `PERMISSION_WRITE` |
| GET/PUT/DELETE | `/api/permissions/{id}` | Bearer | `PERMISSION_READ` / `PERMISSION_WRITE` |

## Design notes
- Access tokens expire in 15 min, refresh tokens in 7 days. Refresh rotates the token, and the used refresh token is revoked.
- Logout revokes the access token (and the refresh token if sent) via the `revoked_tokens` table (jti denylist).
- Deletes are soft deletes (`deleted_at`) via `@SQLDelete` and `@SQLRestriction`.
- Services return DTOs, so no lazy loading happens outside a transaction. `default_batch_fetch_size` avoids N+1 queries.
- Errors use RFC 7807 `ProblemDetail`.
- Soft-deleted users/roles still hold their unique names, so a new record cannot reuse them. Purge or rename them if needed.
- Expired rows in `revoked_tokens` can be pruned with a scheduled job (not included).
