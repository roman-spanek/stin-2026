package cz.tul.stin.orderservice.refactored;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderServiceTest {
    @Test
    void testWeekendDiscount() {

        var saturday = LocalDateTime.of(2024, 6, 15, 10, 0);
        /*
        powershell -NoProfile -Command "[datetime]::new(2024,6,15,10,0,0).DayOfWeek"
        Saturday
         */

        OrderService service =
                new OrderService(new FakeTimeProvider(saturday));

        double result = service.calculateFinalPrice(100);

        assertEquals(80, result);
    }

    @Test
    void testNoDiscountOnWeekday() {

        var monday = LocalDateTime.of(2024, 6, 17, 10, 0);

        OrderService service =
                new OrderService(new FakeTimeProvider(monday));

        double result = service.calculateFinalPrice(100);

        assertEquals(100, result);
    }

    @Test
    void testInvalidPrice() {

        var monday = LocalDateTime.of(2024, 6, 17, 10, 0);

        OrderService service =
                new OrderService(new FakeTimeProvider(monday));

        assertThrows(IllegalArgumentException.class,
                () -> service.calculateFinalPrice(0));
    }
}
