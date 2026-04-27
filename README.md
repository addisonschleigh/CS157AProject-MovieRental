# Movie Rental Database System

A three-tier movie rental application built for CS 157A.

- **Presentation Layer:** Java Swing desktop GUI
- **Application Layer:** Java DAO classes that execute SQL via JDBC
- **Data Layer:** MySQL relational database (BCNF-normalized)

## Requirements

- MySQL Server 8.0 or later
- Java JDK 11 or later
- MySQL Connector/J (mysql-connector-j-8.x.jar)

## Setup

### 1. Create the database

From the project root:

```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/data.sql
```

This creates the `movie_rental` database with five BCNF-normalized tables and loads sample data (15+ rows per table, no NULLs).

### 2. Configure credentials

Edit `src/db/DatabaseConnection.java` if your MySQL username/password differ from the defaults:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/movie_rental";
private static final String USER     = "root";
private static final String PASSWORD = "password";
```

### 3. Compile

From the `src/` directory:

```bash
cd src
javac -cp ".:../lib/mysql-connector-j-8.0.33.jar" db/*.java model/*.java dao/*.java ui/*.java
```

(Use `;` instead of `:` on Windows.)

### 4. Run

```bash
java -cp ".:../lib/mysql-connector-j-8.0.33.jar" ui.MovieRentalApp
```

## Project Structure

```
MovieRentalSystem/
├── sql/
│   ├── schema.sql      CREATE TABLE statements (5 tables, BCNF, NOT NULL constraints)
│   └── data.sql        INSERT statements (15+ rows per table)
├── src/
│   ├── db/
│   │   └── DatabaseConnection.java
│   ├── model/          Plain Java objects (Movie, Customer, Rental, Return, Rating)
│   ├── dao/            Data Access Objects with CRUD methods
│   └── ui/             Swing panels (one per entity) plus MovieRentalApp main class
└── README.md
```

## Features

The GUI has five tabs, one per entity. Each tab supports:

- **SELECT** — Show All / Search / Filter
- **INSERT** — Add new records via form dialog
- **UPDATE** — Edit selected row
- **DELETE** — Remove selected row (with confirmation)

Additional functionality:

- Movies tab: Search by title (LIKE pattern). **Rent This Movie** button — select a movie, pick a customer from a dropdown, and the system creates a rental record while decrementing CopiesAvailable.
- Customers tab: Search by first or last name
- Rentals tab: Filter active rentals only. **Process Return** button — select an active rental, the system auto-calculates a late fee based on today vs the due date, then creates a Return record, marks the rental as "Returned", and increments CopiesAvailable on the movie.
- Ratings tab: Filter by movie, compute average score
