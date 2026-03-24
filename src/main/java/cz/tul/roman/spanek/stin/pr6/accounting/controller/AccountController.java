package cz.tul.roman.spanek.stin.pr6.accounting.controller;

import cz.tul.roman.spanek.stin.pr6.accounting.model.TransactionRequest;
import cz.tul.roman.spanek.stin.pr6.accounting.model.TransactionResponse;
import cz.tul.roman.spanek.stin.pr6.accounting.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * POST /api/accounts/transaction
     *
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

        TransactionResponse response = accountService.applyTransaction(request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInsufficientFunds(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

