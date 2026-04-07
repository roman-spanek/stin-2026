package cz.tul.roman.spanek.stin.pr6.accounting.service;

import cz.tul.roman.spanek.stin.pr6.accounting.model.ClientAccount;
import cz.tul.roman.spanek.stin.pr6.accounting.model.Currency;
import cz.tul.roman.spanek.stin.pr6.accounting.model.TransactionRequest;
import cz.tul.roman.spanek.stin.pr6.accounting.model.TransactionResponse;
import cz.tul.roman.spanek.stin.pr6.accounting.persistence.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

	@Mock
    private AccountRepository repository;

	@Test
	void applyTransactionCreatesNewAccountAndSavesUpdatedState() {

		when(repository.loadAccounts()).thenReturn(new HashMap<>());
		AccountService service = new AccountService(repository);

		TransactionResponse response = service.applyTransaction(new TransactionRequest(
				"ACC-200",
				new BigDecimal("100.00"),
				Currency.CZK,
				LocalDate.of(2026, 3, 31)
		));

		assertEquals(new BigDecimal("100.00"), response.getNewBalance());
		assertEquals("ACC-200", response.getAccountNumber());

		ArgumentCaptor<Map<String, ClientAccount>> captor = ArgumentCaptor.forClass(Map.class);
		verify(repository).saveAccounts(captor.capture());
		assertEquals(new BigDecimal("100.00"), captor.getValue().get("ACC-200").getAmount());
		verify(repository, times(1)).loadAccounts();
	}

    @Test
    void loadAccountsFailureIsPropagatedFromRepository() {
        IllegalStateException loadFailure = new IllegalStateException(
                "Could not load accounts from file: C:/data/accounts.json",
                new RuntimeException("broken json")
        );
        when(repository.loadAccounts()).thenThrow(loadFailure);
        AccountService service = new AccountService(repository);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                service.applyTransaction(new TransactionRequest(
                        "ACC-600",
                        new BigDecimal("10.00"),
                        Currency.CZK,
                        LocalDate.of(2026, 4, 1)
                ))
        );

        assertSame(loadFailure, exception);
        verify(repository, never()).saveAccounts(anyMap());
    }

	@Test
	void applyTransactionWithdrawsFromExistingAccountAndSaves() {
		Map<String, ClientAccount> initialState = new HashMap<>();
		initialState.put("ACC-300", new ClientAccount("ACC-300", Currency.CZK, new BigDecimal("100.00")));
		when(repository.loadAccounts()).thenReturn(initialState);
		AccountService service = new AccountService(repository);

		TransactionResponse response = service.applyTransaction(new TransactionRequest(
				"ACC-300",
				new BigDecimal("-40.00"),
				Currency.CZK,
				LocalDate.of(2026, 3, 31)
		));

		assertEquals(new BigDecimal("60.00"), response.getNewBalance());

		ArgumentCaptor<Map<String, ClientAccount>> captor = ArgumentCaptor.forClass(Map.class);
		verify(repository).saveAccounts(captor.capture());
		assertEquals(new BigDecimal("60.00"), captor.getValue().get("ACC-300").getAmount());
	}

	@Test
	void failedWithdrawalDoesNotSaveState() {
		when(repository.loadAccounts()).thenReturn(new HashMap<>());
		AccountService service = new AccountService(repository);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
				service.applyTransaction(new TransactionRequest(
						"ACC-300",
						new BigDecimal("-10.00"),
						Currency.CZK,
						LocalDate.of(2026, 3, 31)
				))
		);

		assertTrue(exception.getMessage().contains("Insufficient funds"));
		verify(repository, never()).saveAccounts(anyMap());
	}

	@Test
	void failedSaveOnExistingAccountRollsBackInMemoryState() {
		Map<String, ClientAccount> initialState = new HashMap<>();
		initialState.put("ACC-400", new ClientAccount("ACC-400", Currency.CZK, new BigDecimal("100.00")));
		when(repository.loadAccounts()).thenReturn(initialState);
		doThrow(new RuntimeException("Write failed")).doNothing().when(repository).saveAccounts(anyMap());
		AccountService service = new AccountService(repository);

		assertThrows(RuntimeException.class, () -> service.applyTransaction(new TransactionRequest(
				"ACC-400",
				new BigDecimal("50.00"),
				Currency.EUR,
				LocalDate.of(2026, 3, 31)
		)));

		TransactionResponse response = service.applyTransaction(new TransactionRequest(
				"ACC-400",
				new BigDecimal("10.00"),
				Currency.CZK,
				LocalDate.of(2026, 4, 1)
		));

		assertEquals(new BigDecimal("110.00"), response.getNewBalance());
		assertEquals(Currency.CZK, response.getCurrency());
		verify(repository, times(1)).loadAccounts();
	}



	@Test
	void failedSaveOnNewAccountRemovesTransientAccountFromCache() {
		when(repository.loadAccounts()).thenReturn(new HashMap<>());
		doThrow(new RuntimeException("Write failed")).doNothing().when(repository).saveAccounts(anyMap());
		AccountService service = new AccountService(repository);

		assertThrows(RuntimeException.class, () -> service.applyTransaction(new TransactionRequest(
				"ACC-500",
				new BigDecimal("50.00"),
				Currency.CZK,
				LocalDate.of(2026, 3, 31)
		)));

		TransactionResponse response = service.applyTransaction(new TransactionRequest(
				"ACC-500",
				new BigDecimal("20.00"),
				Currency.CZK,
				LocalDate.of(2026, 4, 1)
		));

		assertEquals(new BigDecimal("20.00"), response.getNewBalance());
		verify(repository, times(1)).loadAccounts();
	}
}

