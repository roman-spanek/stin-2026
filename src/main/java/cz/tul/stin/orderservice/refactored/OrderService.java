package cz.tul.stin.orderservice.refactored;

public class OrderService {

    private final TimeProvider timeProvider; //DI

    public OrderService(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public double calculateFinalPrice(double price) {

        if (price <= 0) {
            throw new IllegalArgumentException("Invalid price");
        }

        var now = timeProvider.now();

        double discount = 0;

        if (now.getDayOfWeek().getValue() >= 6) {
            discount = 0.2;
        }

        return price * (1 - discount);
    }
}
