package cz.tul.stin.orderservice.refactored;

import java.time.LocalDateTime;

public class FakeTimeProvider implements TimeProvider {

    private final LocalDateTime fixedTime;

    public FakeTimeProvider(LocalDateTime fixedTime) {
        this.fixedTime = fixedTime;
    }

    @Override
    public LocalDateTime now() {
        return fixedTime;
    }
}
