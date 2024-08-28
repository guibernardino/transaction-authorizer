package br.com.caju.transaction_authorizer.application.api.v1;

import br.com.caju.transaction_authorizer.application.usecase.AuthorizeTransactionUseCase;
import br.com.caju.transaction_authorizer.application.usecase.dto.AuthorizeTransactionInput;
import br.com.caju.transaction_authorizer.application.usecase.dto.AuthorizeTransactionOutput;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {TransactionController.class})
public class TransactionControllerTest {

    @MockBean
    private AuthorizeTransactionUseCase authorizeTransactionUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAuthorizeTransaction_Success() throws Exception {
        AuthorizeTransactionInput input = new AuthorizeTransactionInput(1L, "5411", new BigDecimal("50.00"), "Test Merchant");
        AuthorizeTransactionOutput output = AuthorizeTransactionOutput.approved();

        when(authorizeTransactionUseCase.execute(any(AuthorizeTransactionInput.class)))
                .thenReturn(output);

        String inputJson = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/api/v1/transactions/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorized").value(true))
                .andExpect(jsonPath("$.code").value("00"))
                .andExpect(jsonPath("$.message").value("Approved"));
    }

    @Test
    void testAuthorizeTransaction_InsufficientFunds() throws Exception {
        AuthorizeTransactionInput input = new AuthorizeTransactionInput(1L, "5411", new BigDecimal("50.00"), "Test Merchant");
        AuthorizeTransactionOutput output = AuthorizeTransactionOutput.insufficientFunds();

        when(authorizeTransactionUseCase.execute(any(AuthorizeTransactionInput.class)))
                .thenReturn(output);

        String inputJson = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/api/v1/transactions/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorized").value(false))
                .andExpect(jsonPath("$.code").value("51"))
                .andExpect(jsonPath("$.message").value("Insufficient Funds"));
    }

    @Test
    void testAuthorizeTransaction_FailedTransaction() throws Exception {
        AuthorizeTransactionInput input = new AuthorizeTransactionInput(1L, "5411", new BigDecimal("50.00"), "Test Merchant");
        AuthorizeTransactionOutput output = AuthorizeTransactionOutput.transactionFailed();

        when(authorizeTransactionUseCase.execute(any(AuthorizeTransactionInput.class)))
                .thenReturn(output);

        String inputJson = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/api/v1/transactions/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorized").value(false))
                .andExpect(jsonPath("$.code").value("07"))
                .andExpect(jsonPath("$.message").value("Transaction Failed"));
    }
}