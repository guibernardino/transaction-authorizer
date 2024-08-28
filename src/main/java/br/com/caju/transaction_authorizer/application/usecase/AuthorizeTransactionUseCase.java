package br.com.caju.transaction_authorizer.application.usecase;

import br.com.caju.transaction_authorizer.application.usecase.dto.AuthorizeTransactionInput;
import br.com.caju.transaction_authorizer.application.usecase.dto.AuthorizeTransactionOutput;
import br.com.caju.transaction_authorizer.application.service.BalanceService;
import br.com.caju.transaction_authorizer.application.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthorizeTransactionUseCase {
    CategoryService categoryService;
    BalanceService balanceService;

    @Autowired
    public AuthorizeTransactionUseCase(CategoryService categoryService, BalanceService balanceService) {
        this.categoryService = categoryService;
        this.balanceService = balanceService;
    }

    public AuthorizeTransactionOutput execute(AuthorizeTransactionInput input) {
        try {
            boolean isApproved = categoryService
                    .findCategoriesWithFallback(input.merchant(), input.mcc()).stream()
                    .anyMatch(category -> balanceService.attemptDebit(input.account(), category, input.totalAmount()));

            return isApproved ? AuthorizeTransactionOutput.approved() : AuthorizeTransactionOutput.insufficientFunds();
        } catch (Exception e) {
            return AuthorizeTransactionOutput.transactionFailed();
        }
    }
}
