package br.com.caju.transaction_authorizer.application.service;

import br.com.caju.transaction_authorizer.application.observability.IObservabilityService;
import br.com.caju.transaction_authorizer.application.observability.MetricCounter;
import br.com.caju.transaction_authorizer.domain.entity.Account;
import br.com.caju.transaction_authorizer.domain.entity.Balance;
import br.com.caju.transaction_authorizer.domain.exception.BalanceUpdateFailureException;
import br.com.caju.transaction_authorizer.domain.repository.IBalanceRepository;
import br.com.caju.transaction_authorizer.domain.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {
    @Mock
    private IBalanceRepository balanceRepository;

    @Mock
    private IObservabilityService observabilityService;

    @InjectMocks
    private BalanceService balanceService;

    private final Account account = new Account(1L, "Test Account");
    private final Balance balance = new Balance(1L, account, Category.FOOD, new BigDecimal("100.00"));

    @Test
    void testAttemptDebit_SufficientBalance() {
        when(balanceRepository.findByAccountIdAndCategory(anyLong(), any(Category.class)))
                .thenReturn(Optional.of(balance));

        when(balanceRepository.updateAmount(anyLong(), any(BigDecimal.class)))
                .thenReturn(1);

        boolean result = balanceService.attemptDebit(1L, Category.FOOD, new BigDecimal("50.00"));

        assertTrue(result, "Debit should be allowed with sufficient balance");
        verify(observabilityService, never()).incrementCounter(MetricCounter.INSUFFICIENT_FUNDS);
        verify(observabilityService, never()).incrementCounter(MetricCounter.UPDATE_FAILURE);
    }

    @Test
    void testAttemptDebit_InsufficientBalance() {
        when(balanceRepository.findByAccountIdAndCategory(anyLong(), any(Category.class)))
                .thenReturn(Optional.of(balance));

        boolean result = balanceService.attemptDebit(1L, Category.FOOD, new BigDecimal("150.00"));

        assertFalse(result, "Debit should not be allowed with insufficient balance");
        verify(observabilityService, times(1)).incrementCounter(MetricCounter.INSUFFICIENT_FUNDS);
        verify(observabilityService, never()).incrementCounter(MetricCounter.UPDATE_FAILURE);
    }

    @Test
    void testAttemptDebit_BalanceNotFound() {
        when(balanceRepository.findByAccountIdAndCategory(anyLong(), any(Category.class)))
                .thenReturn(Optional.empty());

        boolean result = balanceService.attemptDebit(1L, Category.FOOD, new BigDecimal("50.00"));

        assertFalse(result, "Debit should not be allowed when balance is not found");
        verify(observabilityService, never()).incrementCounter(MetricCounter.INSUFFICIENT_FUNDS);
        verify(observabilityService, never()).incrementCounter(MetricCounter.UPDATE_FAILURE);
    }

    @Test
    void testAttemptDebit_ShouldThrowException_WhenUpdateFails() {
        when(balanceRepository.findByAccountIdAndCategory(anyLong(), any(Category.class)))
                .thenReturn(Optional.of(balance));

        when(balanceRepository.updateAmount(anyLong(), any(BigDecimal.class)))
                .thenReturn(0);

        assertThrows(BalanceUpdateFailureException.class, () ->
                balanceService.attemptDebit(1L, Category.FOOD, new BigDecimal("50.00")),
                "Expected BalanceUpdateFailureException when update fails");
        verify(observabilityService, times(1)).incrementCounter(MetricCounter.UPDATE_FAILURE);
        verify(observabilityService, never()).incrementCounter(MetricCounter.INSUFFICIENT_FUNDS);
    }
}
