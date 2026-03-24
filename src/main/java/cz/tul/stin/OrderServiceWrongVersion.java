package cz.tul.stin;

import java.time.LocalDateTime;

public class OrderServiceWrongVersion {

    public double calculateFinalPrice(double price) {

        if (price <= 0) {
            System.out.println("Invalid price");
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();

        double discount = 0;

        // víkendová sleva
        if (now.getDayOfWeek().getValue() >= 6) {
            discount = 0.2;
        }

        return price * (1 - discount);
    }
}
