package cz.tul.roman.spanek.stin.pr6.accounting.controller;

import cz.tul.roman.spanek.stin.pr6.accounting.model.TransactionRequest;
import cz.tul.roman.spanek.stin.pr6.accounting.model.TransactionResponse;
import cz.tul.roman.spanek.stin.pr6.accounting.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * POST /api/accounts/transaction
     * Request body example:
     * {
     *   "accountNumber": "123456789",
     *   "amount": 500.00,        // positive = deposit, negative = withdrawal
     *   "currency": "CZK",
     *   "operationDate": "2026-03-24"
     * }
     */
    @PostMapping("/transaction")
    public ResponseEntity<TransactionResponse> processTransaction(
            @RequestBody TransactionRequest request) {
        log.info(
                "Transaction request received: accountNumber={}, amount={}, currency={}, operationDate={}",
                request.getAccountNumber(),
                request.getAmount(),
                request.getCurrency(),
                request.getOperationDate()
        );

        TransactionResponse response = accountService.applyTransaction(request);
        log.info(
                "Transaction processed: accountNumber={}, newBalance={}, currency={}, operationDate={}, message={}",
                response.getAccountNumber(),
                response.getNewBalance(),
                response.getCurrency(),
                response.getOperationDate(),
                response.getMessage()
        );

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInsufficientFunds(IllegalArgumentException ex) {
        log.warn("Transaction validation failed: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

