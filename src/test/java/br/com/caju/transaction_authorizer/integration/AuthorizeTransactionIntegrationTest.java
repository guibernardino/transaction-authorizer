package br.com.caju.transaction_authorizer.integration;

import br.com.caju.transaction_authorizer.application.usecase.dto.AuthorizeTransactionInput;
import br.com.caju.transaction_authorizer.domain.entity.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
public class AuthorizeTransactionIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DirtiesContext
    @Sql("/testdata/initial-data.sql")
    void testAuthorizeTransaction_ShouldDeductAmountFromBalance() throws Exception {
        // Arrange
        long accountId = 1L;
        String merchantName = "Test Merchant";
        String mcc = "5411";
        BigDecimal amount = new BigDecimal("50.00");

        AuthorizeTransactionInput input = new AuthorizeTransactionInput(accountId, mcc, amount, merchantName);
        String inputJson = objectMapper.writeValueAsString(input);

        // Act
        mockMvc.perform(post("/api/v1/transactions/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isOk());

        // Assert
        BigDecimal newBalanceAmount = getAmountByAccountAndCategory(accountId, Category.FOOD);

        BigDecimal expectedNewBalance = new BigDecimal("50.00");
        assertEquals(expectedNewBalance, newBalanceAmount, "The balance should be updated correctly.");
    }

    @Test
    @DirtiesContext
    @Sql("/testdata/initial-data.sql")
    void testAuthorizeTransaction_ShouldNotAlterBalance_WhenInsufficientFunds() throws Exception {
        // Arrange
        long accountId = 1L;
        String merchantName = "Test Merchant";
        String mcc = "5411";
        BigDecimal amount = new BigDecimal("150.00");

        AuthorizeTransactionInput input = new AuthorizeTransactionInput(accountId, mcc, amount, merchantName);
        String inputJson = objectMapper.writeValueAsString(input);

        // Act
        mockMvc.perform(post("/api/v1/transactions/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorized").value(false))
                .andExpect(jsonPath("$.code").value("51"))
                .andExpect(jsonPath("$.message").value("Insufficient Funds"));

        // Assert
        BigDecimal newBalanceAmount = getAmountByAccountAndCategory(accountId, Category.FOOD);

        BigDecimal expectedBalance = new BigDecimal("100.00");
        assertEquals(expectedBalance, newBalanceAmount, "The balance should remain unchanged due to insufficient funds.");
    }

    private BigDecimal getAmountByAccountAndCategory(long accountId, Category category) {
        return jdbcTemplate.queryForObject(
                "SELECT total_amount FROM balance WHERE account_id = ? AND category = ?",
                new Object[]{accountId, category.ordinal()},
                BigDecimal.class
        );
    }
}
