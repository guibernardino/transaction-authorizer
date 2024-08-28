package br.com.caju.transaction_authorizer.domain.exception;

public class BalanceUpdateFailureException extends RuntimeException {
    public BalanceUpdateFailureException(String message) {
        super(message);
    }
}
