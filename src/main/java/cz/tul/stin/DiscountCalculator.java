package cz.tul.stin;

public class DiscountCalculator {

    public double calculateDiscount(double price, boolean isStudent, boolean isPremium) {

        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        double discount = 0;

        if (isStudent) {
            discount += 0.1; // 10 %
        }

        if (isPremium) {
            discount += 0.2; // 20 %
        }

        if (discount > 0.25) {
            discount = 0.25; // max 25 %
        }

        return price * (1 - discount);
    }
}