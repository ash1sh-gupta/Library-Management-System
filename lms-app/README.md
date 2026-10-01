# Library Management System

A fully functional Library Management System built using Java and Spring Boot.

## Features
- **Dashboard**: View total books, available books, issued books, and total members.
- **Book Management**: Add, edit, delete, and search books. Automatically tracks available quantities.
- **Member Management**: Add, edit, delete, and search library members.
- **Issue & Return**: Issue books to members and process returns. Unavailable books cannot be issued.
- **Records**: View all issue and return records. Filter by status or name.
- **Database**: Uses an embedded H2 relational database that persists data to a local file (`data/lmsdb`).

## Tech Stack
- **Backend**: Java 17, Spring Boot, Spring JDBC
- **Frontend**: HTML5, CSS3, Thymeleaf, Bootstrap 5, FontAwesome
- **Database**: H2 (file-based persistence)

## Prerequisites
- Java 17 installed
- Maven installed

## How to Run
1. Open a terminal in the `lms-app` directory.
2. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```
3. Open a web browser and navigate to:
   http://localhost:8080

Sample data (Books and Members) is automatically loaded upon initialization.

## Architecture
- **Model**: Simple POJOs (`Book`, `Member`, `IssueRecord`)
- **Repository**: Spring JDBC for raw SQL execution (`JdbcBookRepository`, etc.)
- **Service**: Business logic and transaction management (`LmsService`)
- **Controller**: Web request handling (`AppController`)
- **UI**: Server-side rendered HTML using Thymeleaf templates.
