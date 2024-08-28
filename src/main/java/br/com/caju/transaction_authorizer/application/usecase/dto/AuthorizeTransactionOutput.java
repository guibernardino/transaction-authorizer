package br.com.caju.transaction_authorizer.application.usecase.dto;

public record AuthorizeTransactionOutput(boolean authorized, String code, String message) {
    public static AuthorizeTransactionOutput approved() {
        return new AuthorizeTransactionOutput(true, "00", "Approved");
    }

    public static AuthorizeTransactionOutput insufficientFunds() {
        return new AuthorizeTransactionOutput(false, "51", "Insufficient Funds");
    }

    public static AuthorizeTransactionOutput transactionFailed() {
        return new AuthorizeTransactionOutput(false, "07", "Transaction Failed");
    }
}
