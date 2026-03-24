package cz.tul.stin.orderservice.refactored;

import java.time.LocalDateTime;


public interface TimeProvider {
    LocalDateTime now();
}

