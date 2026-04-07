package cz.tul.roman.spanek.stin.pr6.accounting.service;

import cz.tul.roman.spanek.stin.pr6.accounting.model.ClientAccount;
import cz.tul.roman.spanek.stin.pr6.accounting.model.Currency;
import cz.tul.roman.spanek.stin.pr6.accounting.model.TransactionRequest;
import cz.tul.roman.spanek.stin.pr6.accounting.model.TransactionResponse;
import cz.tul.roman.spanek.stin.pr6.accounting.persistence.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final Map<String, ClientAccount> accounts = new HashMap<>();
    private boolean accountsLoaded;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /* No DI in place
    public AccountService() {
        this.accountRepository = new FileAccountRepository(new ObjectMapper(), "Accounts.json");
    }
    */


    public synchronized TransactionResponse applyTransaction(TransactionRequest request) {
        loadAccountsIfNeeded();

        String accountNumber = request.getAccountNumber();
        ClientAccount account = accounts.get(accountNumber);
        boolean newAccount = account == null;

        if (newAccount) {
            account = new ClientAccount(accountNumber, request.getCurrency(), BigDecimal.ZERO);
        }

        BigDecimal newBalance = account.getAmount().add(request.getAmount());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient funds: balance would become " + newBalance
            );
        }

        Currency previousCurrency = account.getCurrency();
        BigDecimal previousAmount = account.getAmount();

        account.setAmount(newBalance);
        account.setCurrency(request.getCurrency());

        if (newAccount) {
            accounts.put(accountNumber, account);
        }

        try {
            accountRepository.saveAccounts(accounts);
        } catch (RuntimeException exception) {
            if (newAccount) {
                accounts.remove(accountNumber);
            } else {
                account.setCurrency(previousCurrency);
                account.setAmount(previousAmount);
            }

            throw exception;
        }

        String operation = request.getAmount().compareTo(BigDecimal.ZERO) >= 0 ? "Deposit" : "Withdrawal";

        return new TransactionResponse(
                account.getAccountNumber(),
                newBalance,
                account.getCurrency(),
                request.getOperationDate(),
                operation + " applied successfully."
        );
    }

    private void loadAccountsIfNeeded() {
        if (accountsLoaded) {
            return;
        }

        accounts.clear();
        accounts.putAll(accountRepository.loadAccounts());
        accountsLoaded = true;
    }
}

