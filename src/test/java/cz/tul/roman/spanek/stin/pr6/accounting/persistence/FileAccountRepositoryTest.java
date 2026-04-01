package cz.tul.roman.spanek.stin.pr6.accounting.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.tul.roman.spanek.stin.pr6.accounting.model.ClientAccount;
import cz.tul.roman.spanek.stin.pr6.accounting.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileAccountRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void loadAccountsReturnsEmptyMapWhenFileDoesNotExist() {
        FileAccountRepository repository = new FileAccountRepository(
                new ObjectMapper().findAndRegisterModules(),
                tempDir.resolve("accounts.json").toString()
        );

        assertTrue(repository.loadAccounts().isEmpty());
    }

    @Test
    void saveAccountsWritesDataThatCanBeLoadedAgain() {
        Path storagePath = tempDir.resolve("accounts.json");
        FileAccountRepository repository = new FileAccountRepository(
                new ObjectMapper().findAndRegisterModules(),
                storagePath.toString()
        );

        Map<String, ClientAccount> accounts = new LinkedHashMap<>();
        accounts.put("ACC-100", new ClientAccount("ACC-100", Currency.CZK, new BigDecimal("150.50")));

        repository.saveAccounts(accounts);
        Map<String, ClientAccount> loadedAccounts = repository.loadAccounts();

        assertEquals(1, loadedAccounts.size());
        assertEquals(new BigDecimal("150.50"), loadedAccounts.get("ACC-100").getAmount());
        assertEquals(Currency.CZK, loadedAccounts.get("ACC-100").getCurrency());
        assertTrue(Files.exists(storagePath));
    }

    @Test
    void loadAccountsThrowsClearErrorForCorruptedJson() throws Exception {
        Path storagePath = tempDir.resolve("accounts.json");
        Files.writeString(storagePath, "{not-valid-json}");

        FileAccountRepository repository = new FileAccountRepository(
                new ObjectMapper().findAndRegisterModules(),
                storagePath.toString()
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class, repository::loadAccounts);

        assertTrue(exception.getMessage().contains(storagePath.toAbsolutePath().toString()));
        assertTrue(exception.getMessage().startsWith("Could not load accounts from file:"));
        assertInstanceOf(IOException.class, exception.getCause());
    }
}

