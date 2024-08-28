CREATE TABLE transaction
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(38,2) NOT NULL,
    category TINYINT NOT NULL,
    timestamp DATETIME(6) NOT NULL,
    account_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    CONSTRAINT FK_TRANSACTION_ACCOUNT
        FOREIGN KEY (account_id) REFERENCES account (id),
    CONSTRAINT FK_TRANSACTION_MERCHANT
        FOREIGN KEY (merchant_id) REFERENCES merchant (id)
);

