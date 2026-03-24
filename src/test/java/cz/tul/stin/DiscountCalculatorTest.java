package cz.tul.stin;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiscountCalculatorTest {

    DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void testStudentDiscount() {
        double result = calculator.calculateDiscount(100, true, false);
        assertEquals(90, result);
    }

    @Test
    void testNoDiscount() {
        assertEquals(100,
                calculator.calculateDiscount(100, false, false));
    }

    @Test
    void testPremiumDiscount() {
        assertEquals(80,
                calculator.calculateDiscount(100, false, true));
    }

    @Test
    void testMaxDiscountCap() {
        assertEquals(75,
                calculator.calculateDiscount(100, true, true));
    }

    @Test
    void testInvalidPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateDiscount(0, false, false));
    }

    @Test
    void testNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateDiscount(-50, false, false));
    }

    @Test
    void testBothDiscountsApply() {
        // Student (10%) + Premium (20%) = 30% discount
        double result = calculator.calculateDiscount(100, true, true);
        assertEquals(70, result);
    }

    @Test
    void testSmallPrice() {
        double result = calculator.calculateDiscount(10, true, false);
        assertEquals(9, result);
    }

    @Test
    void testLargePrice() {
        double result = calculator.calculateDiscount(1000, false, false);
        assertEquals(1000, result);
    }

    @Test
    void testPrecisionWithDecimals() {
        double result = calculator.calculateDiscount(123.45, true, false);
        assertEquals(111.105, result, 0.0001);
    }
}
