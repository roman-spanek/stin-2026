package cz.tul.roman.spanek.stin._6.accouting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientAccount {

    private String accountNumber;
    private Currency currency = Currency.CZK;
    private BigDecimal amount = BigDecimal.ZERO;
}
