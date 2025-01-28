CREATE TABLE IF NOT EXISTS Transaction (
                            transactionId UUID PRIMARY KEY,
                            accountId UUID NOT NULL,
                            amount NUMERIC(18, 2) NOT NULL,
                            balanceAfterTransaction NUMERIC(18, 2) NOT NULL,
                            currency VARCHAR(3) NOT NULL,
                            direction VARCHAR(10) NOT NULL CHECK (direction IN ('IN', 'OUT')),
                            description TEXT -- I assume that description is not mandatory
);

CREATE INDEX idx_transaction_accountId ON Transaction(accountId);