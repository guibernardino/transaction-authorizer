package br.com.caju.transaction_authorizer.application.usecase.dto;

import java.math.BigDecimal;

public record AuthorizeTransactionInput(long account, String mcc, BigDecimal totalAmount, String merchant) {}
