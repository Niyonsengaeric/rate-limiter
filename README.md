# API Rate Limiter

Corporation X,Y,Z is a tech company that has launched a notification service for sending SMS and E-mail notifications. They are selling this service to different clients and each client has specific limits on the number of requests they can send in a month. Because they are a startup they have a limited amount of infrastructure to serve all clients at peak capacity because their solution has been very successful. Each client has the ability to pay for more requests per second. Corporation X,Y,Z is seeing performance issues on their api, because they haven't implemented the limits that have been set out in the software.

This project is a solution to the API rate limiting problem. It is implemented using Spring Boot, Redis, and PostgreSQL.

## Requirements

The design question is, how should they try to solve these three issues:

1. Too many requests within the same time window from a client
2. Too many requests from a specific client on a per month basis
3. Too many requests across the entire system

## Solution

To solve the upper mentioned problems, I will implement an API rate limiter which is a technique that is used to control the rate of requests sent or received by a server API. It limits the amount of incoming or outgoing traffic, which can improve the performance of the API and make it more stable.

## Technical Implementation
The rate limiting should work for a distributed setup, as the APIs are accessible through a cluster of servers. I used Redis as a distributed cache to store the rate limit counters, as it is a fast, in-memory data store that can be easily integrated with the Spring Boot application.

## Architecture

The project is implemented using Spring Boot. Redis is used for caching the rate limiting data. PostgreSQL is used for storing the client and user data.

## Running the Project

To run the Spring Boot project locally, please follow the steps below:

### Prerequisites

Make sure you have the following software installed on your machine:

- [Java Development Kit (JDK) 1.8 or higher](https://adoptopenjdk.net/) - Download and install the appropriate JDK version for your operating system.

- [PostgreSQL](https://www.postgresql.org/) - Install PostgreSQL on your system and configure the connection details in the application.properties file.

- [Redis](https://redis.io/) - Install Redis on your system and configure the connection details in the application.properties file.

- [Docker](https://www.docker.com/) - Install Docker to run the PostgreSQL and Redis servers.

- [Maven](https://maven.apache.org/) - Install Maven on your system to execute the `mvn test` command.

### Setup

1. Clone the repository:

`git clone https://github.com/Niyonsengaeric/rate-limiter.git`

2. Start the PostgreSQL and Redis servers

`sudo service postgresql start`

`redis-server`

or start it using docker:

- `docker run --name my-postgres -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres`
- `docker run --name my-redis -p 6379:6379 -d redis`
 

3. create a postgres data base (e.g:rate_limiter)

4. Configure the connection details in the application.properties file. Here is an example of how your application.properties file could look like:

```
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/rate_limiter
spring.datasource.username=postgresUser
spring.datasource.password=postgresUserPassword
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=false
spring.redis.host=localhost
spring.redis.port=6379
rate.limit=50
default.time.capacity=5
default.monthly.capacity=100

```

5. Run the project

The application will be accessible at http://localhost:8080.

## API Documentation
The API documentation is available in the Postman API documentation format [here](https://documenter.getpostman.com/view/8164226/2s93eVXZf5).

The following APIs are implemented:

- `/api/v1/auth/register` - POST request to register as client
- `/api/v1/auth/login` - POST request for client login
- `/api/v1/subscribe` - POST request for more requests per second.
- `/api/v1/subscribe` - GET request to get subscribed request per second
- `/api/v1/notification` - POST request to trigger notification

## Testing

- Use a REST client like Postman to test

- Testing is implemented using JUnit and Mockito. To run the tests, use the following command: `mvn test`


## Demo

A demo of the application is available at [live demo](https://rate-limiter-api-pli5.onrender.com/).

## Presentation Deck

The presentation deck for the design is available at [google presentation](https://docs.google.com/presentation/d/1D2rb0oq9Ge8j3Zz2tHlYZO_SrDm6cOAbLU5PR_cMoO8).

## Communication

If you have any questions or suggestions about the project, please feel free to contact me at [niyeric11@gmail.com].
