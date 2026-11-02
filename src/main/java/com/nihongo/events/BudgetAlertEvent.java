package com.nihongo.events;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Versioned JSON contract for monthly spending alerts (topic {@code expense.events.v1}).
 * {@code email} is optional: without it only the in-app realtime notice is delivered.
 */
public record BudgetAlertEvent(int version, String eventId, String type, String userId, String email,
                               String month, long limitAmount, long totalSpent, int percent, int warnPercent,
                               String occurredAt) {
    public static final String WARNING = "WARNING";
    public static final String EXCEEDED = "EXCEEDED";

    public BudgetAlertEvent {
        if (version != 1 || type == null || !Set.of(WARNING, EXCEEDED).contains(type))
            throw new IllegalArgumentException("Unsupported budget alert event");
        if (eventId == null || occurredAt == null)
            throw new IllegalArgumentException("Missing budget alert identity or timestamp");
        UUID.fromString(eventId);
        Instant.parse(occurredAt);
        if (userId == null || userId.isBlank() || month == null || !month.matches("\\d{4}-(0[1-9]|1[0-2])"))
            throw new IllegalArgumentException("Invalid budget alert event");
        if (limitAmount <= 0 || totalSpent < 0 || percent < 0 || warnPercent < 1 || warnPercent > 99)
            throw new IllegalArgumentException("Invalid budget alert amounts");
        if (email != null && !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
            throw new IllegalArgumentException("Invalid recipient");
    }
}
