# Project Name: HATESHIELD

## Description

WorkshopProject GR 15

The goal of the app is to get messages from Instagram API, send them to API PERSPECTIVE to guess they're toxicity and then delete them if they are problematic.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Built With](#built-with)

---

## Prerequisites

Before you begin, make sure you have installed the following:

- **Java 17+** ([Download Java](https://www.oracle.com/java/technologies/javase-downloads.html))
- **Maven** (or Gradle, depending on your project) ([Install Maven](https://maven.apache.org/install.html))
- **PostgreSQL** (or any other compatible database)

## Installation

### 1. Clone the repository
```bash
git clone https://github.com/Gastonpallas/WorkshopI2.git
```

#### Acces to the repository : 

```bash
cd project-name
```
#### Build the project wih maven :

```bash
mvn clean install
```

#### Configure Databse :
Update the application.properties with your information

```bash
# Examples for application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_dataBase
spring.datasource.username=your_username
spring.datasource.password=your_pasword
```


### 2. Running the Application

```bash
mvn spring-boot:run
```

### 3. API Endpoints

| Method | Endpoint                | Description                   |
|---------|-------------------------|-------------------------------|
| `POST`  | `/process`            | Launch the process     |


## 4. Built With

- [**Spring Boot**](https://spring.io/projects/spring-boot) - The framework for the API
- [**Maven**](https://maven.apache.org/) - Project tool
- [**PostgreSQL**](https://www.postgresql.org/) - DataBase
- [**Lombok**](https://projectlombok.org/) - Code generator (getter/setter)


