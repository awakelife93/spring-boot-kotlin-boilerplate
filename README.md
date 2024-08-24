### Spring Boot Boilerplate (Kotlin)

### Environment & Skills

- Application
  - Kotlin 2.0
  - Jdk 21
  - Spring boot 3.3.0
  - Gradle 8.10
  - Spring Security
  - Spring Batch
  - Springdoc OpenAPI
  - Postgresql
  - h2 database (PostgreSQL mode) - localhost environment
  - Jpa
  - QueryDSL
  - Redis
  - Jwt
  - Validation
  - Sentry
  - LogBack
  - Flyway

- Test
  - Spring Boot Starter Test
  - Spring Security
  - Spring Batch
  - Junit 5
  - Mockito Kotlin
  - Instancio
  - h2 database (PostgreSQL mode)
  - Flyway

- Tools
  - Pgadmin

### Project Guide

- common
- domain (post, user, auth)
- security
  - spring security + jwt logic
- utils
- resources
  - prod, dev, local, common, test, secret-{environment}
    - common: Write common variables for the project.
    - test: Create the variables needed for your test environment.
    - secret-{environment}: auth (jwt, api key), database information

### Description

- cors
  - This project used **spring security** rather than WebMvcConfigurer for the cors environment.
- docker-compose
  - If you plan to use it, you need to check the environment variables.
- create spring batch metadata table (localhost, development and production environments.)
  - Run your ddl script or Please refer
    to [github - spring batch ](https://github.com/spring-projects/spring-batch/blob/5.0.x/spring-batch-core/src/main/resources/org/springframework/batch/core/schema-postgresql.sql)
    - Since this project uses postgresql, the spring.batch.jdbc.initialize-schema: always option does not work.
    - localhost & test environment,
      generating [batch-postgresql-metadata-schema.sql](src/main/resources/db/sql/batch-postgresql-metadata-schema.sql).
      - [application-test.yml](src/main/resources/application-test.yml)

### Author

Hyunwoo Park
