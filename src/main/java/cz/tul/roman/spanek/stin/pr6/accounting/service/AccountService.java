package cz.tul.roman.spanek.stin.pr6.accounting.service;

import cz.tul.roman.spanek.stin.pr6.accounting.model.ClientAccount;
import cz.tul.roman.spanek.stin.pr6.accounting.model.TransactionRequest;
import cz.tul.roman.spanek.stin.pr6.accounting.model.TransactionResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccountService {

    /** In-memory store: accountNumber -> ClientAccount */
    private final Map<String, ClientAccount> accounts = new HashMap<>();

    /**
     * Applies a transaction to the account identified by {@code request.accountNumber}.
     * If the account does not exist yet, it is created automatically.
     * A positive amount is a deposit; a negative amount is a withdrawal.
     *
     * @param request the transaction details
     * @return a response with the updated balance
     * @throws IllegalArgumentException when a withdrawal would make the balance negative
     */
    public TransactionResponse applyTransaction(TransactionRequest request) {
        ClientAccount account = accounts.computeIfAbsent(
                request.getAccountNumber(),
                number -> new ClientAccount(number, request.getCurrency(), BigDecimal.ZERO)
        );

        BigDecimal newBalance = account.getAmount().add(request.getAmount());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient funds: balance would become " + newBalance
            );
        }

        account.setAmount(newBalance);
        account.setCurrency(request.getCurrency());

        String operation = request.getAmount().compareTo(BigDecimal.ZERO) >= 0 ? "Deposit" : "Withdrawal";

        return new TransactionResponse(
                account.getAccountNumber(),
                newBalance,
                account.getCurrency(),
                request.getOperationDate(),
                operation + " applied successfully."
        );
    }
}

