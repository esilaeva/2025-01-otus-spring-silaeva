package ru.otus.hw.actuators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.BookRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

// library health indicator
@Component
@RequiredArgsConstructor
public class LibraryHealthIndicator implements HealthIndicator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy   hh:mm a '['VV']'");

    private final BookRepository bookRepository;

    @Override
    public Health health() {
        try {
            long booksCount = bookRepository.count();
            if (booksCount == 0) {
                return Health.down()
                        .status(Status.OUT_OF_SERVICE)
                        .withDetail("reason", "The library contains no books.")
                        .build();
            }
            return Health.up()
                    .withDetails(
                            Map.of(
                                    "message", "The library is operating normally.",
                                    "booksCount", booksCount,
                                    "checkTime", ZonedDateTime.now(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER)
                            ))
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .status(Status.DOWN)
                    .withDetail("reason", "Failed to query the book repository.")
                    .build();
        }
    }
}