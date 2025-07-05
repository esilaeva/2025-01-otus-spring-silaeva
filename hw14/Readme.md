# Spring Batch Data Migration: PostgreSQL to MongoDB

This is a Spring Boot application that demonstrates a complete ETL (Extract, Transform, Load) pipeline using Spring
Batch. It migrates a classic library data model (books, authors, genres) from a relational PostgreSQL database to a
non-relational MongoDB database.

The application uses an in-memory H2 database to store the Spring Batch metadata, keeping it separate from the business
data.

## Key Features

- **Extract:** Reads data in pages from PostgreSQL using `JpaPagingItemReader`.
- **Transform:** Converts the relational entities into corresponding MongoDB document models. It maintains relationships
  by creating references between documents.
- **Load:** Writes the transformed data to MongoDB collections using `MongoItemWriter`.
- **State Management:** Uses Spring Batch's `ExecutionContext` to pass state between steps, ensuring the job is robust
  and restartable.
- **Shell Interface:** Provides a simple command-line interface via Spring Shell to start the migration job and inspect
  data.

## Prerequisites

- Java 17 or newer
- Maven 3.8+
- Docker and Docker Compose

## Running the Required Services

> **Important:** Before running the application, you must start the required PostgreSQL and MongoDB database containers
> using Docker Compose.

Navigate to the root of the project where the `docker-compose.yml` file is located and run the following command:

```shell
  docker-compose up -d
```

This command will start the following services in the background:

-   A **PostgreSQL 15** container on port `5432`.
-   A **pgAdmin 4** container on port `5050` for managing the PostgreSQL database.
-   A **MongoDB 6** container on port `27017`.

To stop the containers, you can run `docker-compose down`.

## Building and Running the Application

1.  **Build the project** using Maven:

2.  **Run the application** from the generated JAR file:

## Available shell commands:
The application provides a set of shell commands to interact with the databases and manage the migration job.
### Data Migration Commands
- **start** (alias: **st**): Starts the full data migration process from PostgreSQL to MongoDB.

### Data Verification Commands
#### PostgreSQL
- **pcheck** (alias: **pc**): Lists all books with their related authors and genres from the PostgreSQL database.
#### MongoDB
- **mcheck** (alias: **mc**): Lists all books with their related authors and genres from the MongoDB database.
- **mclear** (alias: **mcl**): Drop all MongoDB data related to Books, Authors and Genres. Useful for debug jobs. 



