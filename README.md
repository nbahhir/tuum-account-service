# Created by Nikita Bahhir for Tuum interview process
# Overview
To start writing is the hardest part of making any sort of documentation.
I will begin by providing a brief overview of the app.

The task was to create an account service, which would handle accounts, balances and transactions. The app should communicate with PostgreSQL using MyBatis and send events using RabbitMQ.
Both MyBatis and RabbitMQ were new to me, but it was an enjoyable experience.

This is what the overall architecture looks like:
![image](https://github.com/user-attachments/assets/21168545-ea0c-487e-8c9f-bbb997a2e768)

Unfortunately, as with everything, it is not ideal. One thing I am not proud about is that the Services basically have dependency on Database classes (upper-level, like MyBatis, but still.).
If this was a real enterprise app, I would add dependency-inversion between Business and Database layers by abstracting the Mappers under Repository interface, like in the example below.
![image](https://github.com/user-attachments/assets/eddcc50e-0bfc-4d74-8dc4-2a91300b9e4b)

On the first image you can also see the database entities I designed for this app. The requirements didn't specify what exactly do we need to save in each of those models, but I structured it in a way that seemed the most logical and efficient to me.

During the development I made a lot of deliberate choices that either improved maintainability, efficiency or stability. Most of those are explained in the code comment, however, I will also mention the important ones here.

## Service-layer classes responsibilities
First of all, I started with the core business logic, because this is needs to be designed in the first place. Database, endpoint, etc. - are the details that are simply interchangeble modules for the core business logic. From the get-go, I knew that I need to have at least 3 Service-layer classes:
- AccountService
- TransactionService
- BalanceService
Every one of those are designed for one concrete reason and has only one reason to ever change. We change the AccountService only when the logic of accounts changes.

However, the first problem to solve appeared when I began thinking about higher-level logic. One of the requirements is that when a new account is created - corresponding balances are also created. The question is - where to put such logic? It doesn't belong in AccountService. Neither does it belong in BalanceService.
If we were to force AccountService to call BalanceService, we would create a mess and utterly violate the Single-Responsibility-Principle. This logic also doesn't belong in the Controller class. Why should controller be responsbile for assembling this whole package of balances and accounts, calling one service after another? The obvious solution here is to create an higher-level service class, which would be responsible for handling Account-Balance operations. Same logic goes for the Transaction-Balance operations.

## Database shenanigans
Few of my important decisions were made when I was designing the schema for the database. I started working on this even before I learned about how myBatis handles stuff. Started with typical Repository classes, which would later be demolished in favor of MyBatis Mapper classes.

My first real choice here was to decide the type I will use for IDs. The choices were: String, Long, UUIDs. I choose UUID, because I feel like this is the right choice for the enterprise solution. UUID are unique, cannot be guessed by some curious user, supported by postgresql. UUIDs also help to avoid conflicts in distributed system architecture.

I also thought about the data type for amount of money. I chose BigDecimal, as we need prestige accuracy when dealing with finances.

Next, after designing the schemas, which can be seen on the first photo, I added some indexes for faster querying on certain columns and started to think if my tables are secured in distributed systems environment. The vulnerable table here is Balance, since it is the only table, which entries can be updated by design. Here I used pessimistic locking to ensure that some other service instance won't change the balance while it is being processed in another instance/thread. For this I have added FOR UPDATE to the SELECT query in the Balance table.

MyBatis offers two choices of making mappers for database operaions: XML or annotation-based one. I chose annotation-based one, since the queries we are using here are really simple and I felt that XML files were a big overhead.

## Endpoints
Nothing extravagant here. Just used typical REST Controller to define my endpoints. What can be curious here is a lot of regular expressions in the request DTOs, which I added to first-of-all avoid injection attacks in as much places as possible. If there is a way to make the system more secure and reduce the amount of validation code onwards, why not?

I've also defined a Global Exception Handler class to reduce the amount of code in the controller. It helps to centralize the exception handling processes and make sure that user sees relevant error codes.

Here are the list of available endpoints for this app:
- POST /api/accounts/create - This creates an account and correposponding balances. This accepts a body with three fields: customerId (mandatory), country (mandatory) and list of currencies in ISO format (EUR, USD, etc.) (not mandatory). I designed the system so that it allows to create account with no balances, since the requirements didn't specify it. On success, the endpoint returns created accountId, customerId and a list of balances, which include currency and amount (new balances are set to 0 automatically).
- GET /api/accounts/{accountId} - get account by accountId. The only and mandatory field is accountId (which is UUID). Returns the accountId, customerId and a list of balances (currency and amount).
- POST /api/transactions/create - This creates a transaction. Every field is mandatory. The inputs are: accountId, amount of money (should be valid number, max 2 decimal points), currency (should be valid ISO), direction (no more than 10 letters, just to prevent injection) and description (only letters, whitespaces and common punctuation). Transactions change the Balances and thus there are a lot of validation for sufficient funds etc. Returns the created transactionId, accountId, amount of money transfered, currency, direction of transfer, description and overall balance after the transaction.
- GET /api/transactions/{accountId} - Gets all transaction associated with this accountId. Returns the same thing as the previous endpoint.

## Testing
Probably the hardest part for me. For my integration testing, I've decided to use Junit 5's TestContainers to spin up some docker containers for integration testing. I didn't feel good about testing the database operations using the in-memory. Haven't used Testcontainers that much before, but I can say it is fairly simple and comfortable to use.
Unit testing was done using Mockito. Actually, most of the testing code is unit testing. The only integration tests I've done was for the Mappers, Controller (it tests the whole flow) and RabbitMq. I initially also wanted to integration test the AccountBalance and TransactionBalance services, but my unit testing was, in my opinion, really well-done. Also I felt like my Controller integration test was enough to see if the systems are working together as intended.

## RabbitMQ
I decided that the best place to publish the messages would be the lower-level Service classes for Account, Balance and Transaction. They know exacly when the database is updated and are usually part of transaction on higher-level services. This also decouples the publishing logic from the database layer.

For RabbitMQ I chose event-driven architecture with the topic exchange approach. This will allow potential consumers to choose more freely, which queues they want to consume. For example, if consumer only wants Balance update event, it can listen to event.balance.update.

I created 4 queues - 
- AccountEventQueue (routing key event.account.*)
- BalanceEventQueue (routing key event.balance.*)
- TransactionEventQueue (routing key event.transaction.*)
- AllEventsQueue (routing key event.*)

I think now it is time to proceed to other topics, as various technical details of the service could be discussed here forever.

# Instructions on how to build and run applications

## Prerequisites
For the app to run, I used the following version:
- Java 23 (JDK)
- Gradle 8.11.1
- Docker (20.x+) and Docker Compose (2.x+)

## Building the Application
First of all, the repository needs to be cloned. Then, the project needs to be built.
```
./gradlew clean build
```
This way, the built .jar file will be located at ./build/libs/*.jar

## Running locally
You can run the app either by running
```
docker-compose up
```
Since the app itself have Dockerfile and is part of docker container system.

You can also separately run the jar of the build project. However, you will still need to compose-up to run postgres and rabbit mq.
```
java -jar build/libs/the-app-name.jar
```

## Testing
Testing could be run by using the 
```
./gradlew test
```
Command.

# Estimating how many transactions my PC can handle
For the estimation I used k6 tool with hand-written java script code that calls my transaction creation endpoint.

Results can be seen here
![image](https://github.com/user-attachments/assets/bac75d93-8a3b-42e0-8dad-80976837cf51)

Main points here:
- Total Requests: 2235
- Test Duration: 50 seconds
- Average HTTP Response Time: 2.3ms
- Max Virtual Users: 100
- Failed Requests (http_req_failed): 0% (meaning all requests were successfully processed)

Next, if we divide total requests by total test duration time, we get the result of 44.7 transactions per second.

This analysis also showed that the average response time was fairly short (2.3 ms) and that the system could handle 100 users at the same time. Furthermore, there were no failed requests.

# Describe what do you have to consider to be able to scale applications horizontally
Well, there are certainly a lot of things.
First of all, database load will increase drastically when increasing the number of instances.
For this issue, optimizing queries and using cache solution like Redis can help to remedy the situation.

It is also important to consider concurrency questions. With increasing number of instances the chances of conflicts will arise. It is very important code with concurrency in mind. For example, like I used in my application, we can use many mechanisms to avoid concurrency issues. Like pessimistic locking.

Makes sense to think of some gateway for REST requests.

Need to be careful with shared states between instances. Shared data in distributed systems environment is unpredictable and risky. Better to persist data in storage like PostGres and keep away from in-memory storage.

Load between instances can become uneven, and something that manages the load would be needed in this case, like cloud load balancers.

