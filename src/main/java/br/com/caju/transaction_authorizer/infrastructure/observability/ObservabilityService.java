package br.com.caju.transaction_authorizer.infrastructure.observability;

import br.com.caju.transaction_authorizer.application.observability.MetricCounter;
import br.com.caju.transaction_authorizer.application.observability.IObservabilityService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObservabilityService implements IObservabilityService {
    private final MeterRegistry meterRegistry;

    @Autowired
    public ObservabilityService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void incrementCounter(MetricCounter counter) {
        meterRegistry.counter(counter.getCounterName()).increment();
    }
}
