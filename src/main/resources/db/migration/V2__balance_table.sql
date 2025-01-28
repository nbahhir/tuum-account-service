CREATE TABLE IF NOT EXISTS Balance (
                         balanceId UUID PRIMARY KEY,
                         currency VARCHAR(3) NOT NULL, -- all iso4217 are 3 letters
                         accountId UUID NOT NULL,
                         amount NUMERIC(18, 2) NOT NULL
);

CREATE INDEX idx_balance_accountId ON Balance(accountId);