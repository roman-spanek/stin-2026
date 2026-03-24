package cz.tul.stin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderServiceWrongVersionTest {

    @Test
    void testWeekendDiscount() {
        OrderServiceWrongVersion service = new OrderServiceWrongVersion();
        double result = service.calculateFinalPrice(100);
        assertEquals(80, result);
    }

}
