package br.com.caju.transaction_authorizer.application.usecase;

import br.com.caju.transaction_authorizer.application.service.BalanceService;
import br.com.caju.transaction_authorizer.application.service.CategoryService;
import br.com.caju.transaction_authorizer.application.usecase.dto.AuthorizeTransactionInput;
import br.com.caju.transaction_authorizer.application.usecase.dto.AuthorizeTransactionOutput;
import br.com.caju.transaction_authorizer.domain.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorizeTransactionUseCaseTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private AuthorizeTransactionUseCase authorizeTransactionUseCase;

    @Test
    void testExecute_TransactionApproved() {
        AuthorizeTransactionInput input = new AuthorizeTransactionInput(1L, "5411", new BigDecimal("50.00"), "Test Merchant");

        when(categoryService.findCategoriesWithFallback(input.merchant(), input.mcc()))
                .thenReturn(Set.of(Category.FOOD));

        when(balanceService.attemptDebit(anyLong(), any(Category.class), any(BigDecimal.class)))
                .thenReturn(true);

        AuthorizeTransactionOutput result = authorizeTransactionUseCase.execute(input);

        assertEquals(AuthorizeTransactionOutput.approved(), result, "Transaction should be approved when balance is sufficient");
    }

    @Test
    void testExecute_TransactionInsufficientFunds() {
        AuthorizeTransactionInput input = new AuthorizeTransactionInput(1L,"5411", new BigDecimal("50.00"), "Test Merchant");

        when(categoryService.findCategoriesWithFallback(input.merchant(), input.mcc()))
                .thenReturn(Set.of(Category.FOOD));

        when(balanceService.attemptDebit(anyLong(), any(Category.class), any(BigDecimal.class)))
                .thenReturn(false);

        AuthorizeTransactionOutput result = authorizeTransactionUseCase.execute(input);

        assertEquals(AuthorizeTransactionOutput.insufficientFunds(), result, "Transaction should be declined due to insufficient funds");
    }

    @Test
    void testExecute_TransactionFailed() {
        AuthorizeTransactionInput input = new AuthorizeTransactionInput(1L, "5411", new BigDecimal("50.00"), "Test Merchant");

        when(categoryService.findCategoriesWithFallback(input.merchant(), input.mcc()))
                .thenThrow(new RuntimeException("Unexpected error"));

        AuthorizeTransactionOutput result = authorizeTransactionUseCase.execute(input);

        assertEquals(AuthorizeTransactionOutput.transactionFailed(), result, "Transaction should fail due to an exception");
    }
}