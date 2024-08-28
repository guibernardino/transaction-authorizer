CREATE TABLE balance
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category TINYINT NOT NULL,
    total_amount DECIMAL(38,2) NOT NULL,
    account_id BIGINT NOT NULL,
    CONSTRAINT FK_BALANCE_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE INDEX idx_balance_account_category ON balance (account_id, category);