CREATE EXTENSION IF NOT EXISTS "uuid-ossp"; -- in postgres we need to add uuid extension, in case it is not enabled

CREATE TABLE IF NOT EXISTS Account (
                         accountId UUID PRIMARY KEY,
                         country VARCHAR(56) NOT NULL, -- since the longest country name is 56
                         customerId UUID UNIQUE NOT NULL -- here i assume that customer cannot have more than 1 account
);

CREATE INDEX idx_account_country ON Account(country);