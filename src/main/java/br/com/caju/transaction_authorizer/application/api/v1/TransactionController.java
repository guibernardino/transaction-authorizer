package br.com.caju.transaction_authorizer.application.api.v1;

import br.com.caju.transaction_authorizer.application.usecase.AuthorizeTransactionUseCase;
import br.com.caju.transaction_authorizer.application.usecase.dto.AuthorizeTransactionInput;
import br.com.caju.transaction_authorizer.application.usecase.dto.AuthorizeTransactionOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    final AuthorizeTransactionUseCase authorizeTransactionUseCase;

    @Autowired
    public TransactionController(AuthorizeTransactionUseCase authorizeTransactionUseCase) {
        this.authorizeTransactionUseCase = authorizeTransactionUseCase;
    }

    @Operation(summary = "Authorize a transaction", description = "Authorizes transactions by mapping the MCC code to a benefit category and, if approved, deducts the transaction amount from the available balance in the corresponding category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response for processed transaction",
                    content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorizeTransactionOutput.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error while processing the transaction.",
                    content = @Content)
    })
    @PostMapping("/authorize")
    public ResponseEntity<AuthorizeTransactionOutput> authorizeTransaction(@RequestBody AuthorizeTransactionInput input) {
        AuthorizeTransactionOutput output = authorizeTransactionUseCase.execute(input);
        return new ResponseEntity<>(output, HttpStatus.OK);
    }
}
