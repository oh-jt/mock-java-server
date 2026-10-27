# mock-java-server

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
