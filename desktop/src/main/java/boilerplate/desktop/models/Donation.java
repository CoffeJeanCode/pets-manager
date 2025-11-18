// model/Donation.java
package boilerplate.desktop.models;

import java.time.LocalDateTime;

public record Donation(
        Long id,
        String donorName,
        String donorEmail,
        String donationType, // "monetary" | "food" | "medicine" | "toys"
        String amount,
        String description,
        String paymentMethod,
        String transactionReference,
        String amountReceived,
        LocalDateTime donationDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}