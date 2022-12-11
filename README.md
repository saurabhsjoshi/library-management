# Library Management

A microservices based application that allows users to manage a library. It consists of the following services:

1. `inventory-svc` The service that manages inventory of books
2. `user-svc` User management service which also does the authentication/authorization
3. `card-svc` Service to allow users to order physical library cards (WIP)
4. `reservation-svc` Service that manages user reservations
5. `notification-svc` Microservice that reports any reservations that are due soon
6. `nginx-prox` Nginx reverse proxy that acts as the gateway for clients running outside the cluster

## Testing

Two approaches have been explored as part of testing:

1. Cucumber based acceptance tests based on RAATA (Reusable Automated Acceptance Testing Architecture)
2. Dynamically generated test cases using FPTS (Framework for Performance Test Specification)