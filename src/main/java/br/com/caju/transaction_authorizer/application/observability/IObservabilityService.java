package br.com.caju.transaction_authorizer.application.observability;

public interface IObservabilityService {
    void incrementCounter(MetricCounter counter);
}
