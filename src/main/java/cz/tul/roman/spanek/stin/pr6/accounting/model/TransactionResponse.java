package cz.tul.roman.spanek.stin.pr6.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private String accountNumber;
    private BigDecimal newBalance;
    private Currency currency;
    private LocalDate operationDate;
    private String message;
}

