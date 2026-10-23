# mock-java-server

> **Intentionally vulnerable on branch `pr/create-user`. Interview use only. Do not deploy.**

A small identity-style service (users in tenants) used for code-review exercises.

## Run

    mvn spring-boot:run

## Test

    mvn -q test

## Layout

- `user/UserController`      – HTTP endpoints
- `user/UserRepository`      – JDBC access
- `user/CreateUserRequest`   – request model and validation
- `user/UserResponse`        – response model
- `security/PasswordService` – password hashing
