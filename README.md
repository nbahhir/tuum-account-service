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



