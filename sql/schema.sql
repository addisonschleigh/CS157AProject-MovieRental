-- Movie Rental Database System Schema
-- All relations are normalized to BCNF.
-- Every column is declared NOT NULL.

DROP DATABASE IF EXISTS movie_rental;
CREATE DATABASE movie_rental;
USE movie_rental;

CREATE TABLE Movies (
    MovieID         INT AUTO_INCREMENT PRIMARY KEY,
    Title           VARCHAR(150)   NOT NULL,
    Genre           VARCHAR(50)    NOT NULL,
    Director        VARCHAR(100)   NOT NULL,
    ReleaseYear     INT            NOT NULL,
    MPAARating      VARCHAR(10)    NOT NULL,
    CopiesAvailable INT            NOT NULL,
    RentalPrice     DECIMAL(5,2)   NOT NULL
);

CREATE TABLE Customers (
    CustomerID  INT AUTO_INCREMENT PRIMARY KEY,
    FirstName   VARCHAR(50)   NOT NULL,
    LastName    VARCHAR(50)   NOT NULL,
    Email       VARCHAR(100)  NOT NULL UNIQUE,
    Phone       VARCHAR(20)   NOT NULL,
    Address     VARCHAR(200)  NOT NULL,
    JoinDate    DATE          NOT NULL
);

CREATE TABLE Rentals (
    RentalID    INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID  INT          NOT NULL,
    MovieID     INT          NOT NULL,
    RentalDate  DATE         NOT NULL,
    DueDate     DATE         NOT NULL,
    Status      VARCHAR(20)  NOT NULL,
    FOREIGN KEY (CustomerID) 
        REFERENCES Customers(Customer+ID)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    FOREIGN KEY (MovieID)
        REFERENCES Movies(MovieID)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

CREATE TABLE Returns (
    ReturnID    INT AUTO_INCREMENT PRIMARY KEY,
    RentalID    INT           NOT NULL UNIQUE,
    ReturnDate  DATE          NOT NULL,
    LateFee     DECIMAL(5,2)  NOT NULL,
    FOREIGN KEY (RentalID)
        REFERENCES Rentals(RentalID)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

CREATE TABLE Ratings (
    RatingID    INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID  INT   NOT NULL,
    MovieID     INT   NOT NULL,
    Score       INT   NOT NULL,
    ReviewDate  DATE  NOT NULL,
    FOREIGN KEY (CustomerID)
        REFERENCES Customers(CustomerID)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    FOREIGN KEY (MovieID)
        REFERENCES Movies(MovieID)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);
