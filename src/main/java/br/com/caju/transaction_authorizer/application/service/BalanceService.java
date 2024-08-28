package br.com.caju.transaction_authorizer.application.service;

import br.com.caju.transaction_authorizer.application.observability.IObservabilityService;
import br.com.caju.transaction_authorizer.domain.entity.Balance;
import br.com.caju.transaction_authorizer.domain.exception.BalanceUpdateFailureException;
import br.com.caju.transaction_authorizer.application.observability.MetricCounter;
import br.com.caju.transaction_authorizer.domain.repository.IBalanceRepository;
import br.com.caju.transaction_authorizer.domain.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceService {
    IBalanceRepository balanceRepository;
    IObservabilityService observability;

    @Autowired
    public BalanceService(IBalanceRepository balanceRepository, IObservabilityService observability) {
        this.balanceRepository = balanceRepository;
        this.observability = observability;
    }

    @Transactional
    public boolean attemptDebit(long accountId, Category category, BigDecimal amount) {
        return balanceRepository
                .findByAccountIdAndCategory(accountId, category)
                .filter(balance -> hasSufficientAmount(balance, amount))
                .map((balance) -> {
                    subtractAndUpdateOrThrow(balance, amount);
                    return true;
                }).orElse(false);
    }

    private boolean hasSufficientAmount(Balance balance, BigDecimal amount) {
        boolean hasSufficient = balance.getTotalAmount().compareTo(amount) >= 0;
        if (!hasSufficient) {
            observability.incrementCounter(MetricCounter.INSUFFICIENT_FUNDS);
        }
        return hasSufficient;
    }

    private void subtractAndUpdateOrThrow(Balance balance, BigDecimal amount) {
        final BigDecimal newAmount = balance.getTotalAmount().subtract(amount);
        final int rowsUpdated = balanceRepository.updateAmount(balance.getId(), newAmount);
        if (rowsUpdated != 1) {
            observability.incrementCounter(MetricCounter.UPDATE_FAILURE);
            throw new BalanceUpdateFailureException("Failed to update balance. Expected to update 1 row but updated " + rowsUpdated);
        }
    }
}
