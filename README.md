# Spring Boot Boilerplate Project

This is a boilerplate project using **Spring Boot 3.3.4**, **Java 21**, and **IntelliJ IDEA 2024.1.4 (Ultimate Edition)**. It is configured to demonstrate modern Spring Boot application development with Docker, Gradle, and JPA.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Requirements](#requirements)
- [Setup](#setup)
- [Build and Run](#build-and-run)
- [Docker](#docker)
- [Testing](#testing)
- [Contributing](#contributing)

## Features

- **Spring Boot 3.3.4**: The latest version of Spring Boot.
- **Java 21**: Modern language features and improved performance.
- **JPA & Hibernate**: For database management and ORM mapping.
- **JWT Authentication**: Secure authentication using JSON Web Tokens.
- **Docker Integration**: Easily build and run your application in a Docker container.
- **Gradle Build Tool**: For dependency management and building the project.
- **Swagger**: API documentation available through Swagger UI.

## Tech Stack

- **Backend**: Spring Boot 3.3.4
- **Database**: PostgreSQL 16 (via Docker)
- **Build Tool**: Gradle
- **Authentication**: JWT (JSON Web Token)
- **Containerization**: Docker & Docker Compose
- **API Documentation**: Swagger (springdoc-openapi)
- **Java Version**: Java 21.0.4

## Requirements

Before you begin, ensure you have met the following requirements:

- **Java 21**: Ensure Java 21 is installed on your system.
- **IntelliJ IDEA 2024.1.4**: Ultimate Edition for advanced Spring Boot support.
- **Docker**: Docker and Docker Compose should be installed.
- **Gradle**: Gradle 8.x or higher (optional, as the project includes Gradle wrapper).

## Setup

1. **Clone the repository**:

    ```bash
    git clone https://github.com/your-username/spring-boot-boilerplate.git
    cd spring-boot-boilerplate
    ```

2. **Configure the environment**:
    - Set the `application.properties` or `application.yml` in `src/main/resources` to configure the database, ports, and profiles.
    - You can set active profiles via environment variables:

      ```bash
      SPRING_PROFILES_ACTIVE=local
      ```

3. **Setup PostgreSQL** (via Docker Compose):
    ```bash
    docker-compose up -d
    ```

## Build and Run

To build and run the application locally:

### 1. Using IntelliJ IDEA:

- Open the project in **IntelliJ IDEA Ultimate**.
- Navigate to **Run > Edit Configurations** and set up a **Spring Boot** run configuration.
- Click **Run** or **Debug** to start the application.

### 2. Using Command Line:

To build the project:
```bash
./gradlew clean build
```
To run the project:
```bash
./gradlew bootRun
```
## Docker

This project includes a Dockerfile and a docker-compose.yml file for containerization.



* To start the application and database in Docker containers:
    ```bash
    docker-compose up --build -d
    ```

* Access the application at http://localhost:8081.

## Swagger

Swagger UI is available to explore the API:

* URL: http://localhost:8081/swagger-ui.html

## Testing

* To run unit and integration tests:
    ```bash
    ./gradlew test
    ```

* Test reports will be available in the build/reports/tests/test/index.html.

## Contributing

If you’d like to contribute, please fork the repository and make changes as you’d like. Contributions are welcome!

1.	Fork the repository
2. Create a new feature branch (git checkout -b feature-name)
3.	Commit your changes (git commit -m 'Add new feature')
4.	Push to the branch (git push origin feature-name)
5.	Create a pull request

Feel free to customize this README.md file according to your specific project setup.