package cz.tul.roman.spanek.stin.pr6.accounting.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    /** Target account number. */
    private String accountNumber;

    /**
     * Amount of the operation.
     * Positive value = deposit, negative value = withdrawal.
     */
    private BigDecimal amount;

    /** Currency of the transaction. */
    private Currency currency;

    /** Date when the operation takes place. */
    private LocalDate operationDate;
}

