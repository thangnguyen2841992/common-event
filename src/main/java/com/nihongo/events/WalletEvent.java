package com.nihongo.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/** Versioned JSON contract shared by the wallet producer and notification consumers. */
public record WalletEvent(int version, String eventId, String type, Long depositId,
                          String userId, String email, BigDecimal amount, BigDecimal balance,
                          String occurredAt, String reviewNote) {
    public WalletEvent {
        if (version != 1 || type == null || !Set.of("CREATED", "APPROVED", "REJECTED").contains(type))
            throw new IllegalArgumentException("Unsupported wallet event");
        if (eventId == null || occurredAt == null)
            throw new IllegalArgumentException("Missing wallet event identity or timestamp");
        UUID.fromString(eventId);
        Instant.parse(occurredAt);
        if (depositId == null || depositId <= 0 || userId == null || userId.isBlank()
            || amount == null || amount.signum() <= 0 || balance == null || balance.signum() < 0)
            throw new IllegalArgumentException("Invalid wallet event");
        if (email == null || !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
            throw new IllegalArgumentException("Invalid recipient");
        if (reviewNote != null && reviewNote.length() > 500)
            throw new IllegalArgumentException("Review note exceeds 500 characters");
    }

    /** Keep source compatibility for v1 producers that do not supply a review note. */
    public WalletEvent(int version, String eventId, String type, Long depositId,
                       String userId, String email, BigDecimal amount, BigDecimal balance,
                       String occurredAt) {
        this(version, eventId, type, depositId, userId, email, amount, balance, occurredAt, null);
    }
}
