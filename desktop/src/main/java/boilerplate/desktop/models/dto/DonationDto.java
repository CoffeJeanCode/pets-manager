package boilerplate.desktop.models.dto;

import java.time.LocalDateTime;

public record DonationDto(
        Long id,
        String donorName,
        String donorEmail,
        String donationType,
        String amount,
        String amountReceived,
        LocalDateTime donationDate
) {} 
