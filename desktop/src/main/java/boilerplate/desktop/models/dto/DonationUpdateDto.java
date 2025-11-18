package boilerplate.desktop.models.dto;

import java.time.LocalDateTime;

public record DonationUpdateDto(
        String donorName,
        String donorEmail,
        String donationType,
        String amount,
        String description,
        String paymentMethod,
        String transactionReference,
        String amountReceived,
        LocalDateTime donationDate
) {}

