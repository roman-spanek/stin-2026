package cz.tul.roman.spanek.stin.pr6.accounting.persistence;

import cz.tul.roman.spanek.stin.pr6.accounting.model.ClientAccount;

import java.util.Map;

public interface AccountRepository {

    Map<String, ClientAccount> loadAccounts();

    void saveAccounts(Map<String, ClientAccount> accounts);
}

