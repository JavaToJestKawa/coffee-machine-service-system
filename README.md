# Coffee Machine Service System

Spring Boot web application for managing customers, coffee machines and service operations.

## Demo

![Application demo](assets/demo.gif)

## Description

Coffee Machine Service System is a Java web application created as an academic project for modeling and implementing a service management system.  
The application allows users to browse customers, display their registered coffee machines through object associations, and register new coffee machines using a web form.

The project demonstrates object-oriented modeling concepts such as inheritance, associations, composition, class attributes, persistence, validation and layered architecture.

## Features

- Customer list view
- Displaying coffee machines assigned to a selected customer
- Coffee machine registration form
- Form validation
- Unique serial number validation
- Persistent H2 database
- Sample data initialization
- JPA entity relationships
- Inheritance mapping with JOINED strategy
- Composition using cascade and orphan removal
- Thymeleaf-based GUI

## Technologies

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Thymeleaf
- H2 Database
- Jakarta Validation
- Maven
- HTML
- CSS
- JavaScript

## Architecture

The project uses a layered architecture:

- `model` - domain classes and JPA entities
- `repository` - database access using Spring Data JPA
- `service` - application logic and transactions
- `web` - Spring MVC controllers
- `web.form` - form backing objects
- `templates` - Thymeleaf HTML views
- `static` - CSS and JavaScript files

## Main Use Case

The main GUI use case allows the user to:

1. Open the customer list.
2. Select a customer.
3. Display coffee machines associated with that customer.
4. Register a new coffee machine.
5. Save the new machine to the database.
6. See the machine after application restart.

## Persistence

The application uses a file-based H2 database:

```properties
spring.datasource.url=jdbc:h2:file:./data/coffee-service-db
