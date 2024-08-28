package br.com.caju.transaction_authorizer.application.observability;

import lombok.Getter;

@Getter
public enum MetricCounter {
    INSUFFICIENT_FUNDS("balance.insufficient.funds"),
    UPDATE_FAILURE("balance.update.failure");

    private final String counterName;

    MetricCounter(String counterName) {
        this.counterName = counterName;
    }
}