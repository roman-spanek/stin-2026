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
    private String accountNumber;
    private BigDecimal amount;
    private Currency currency;
    private LocalDate operationDate;
}

