package boilerplate.desktop.models.dto;

import java.time.LocalDate;

public record VaccineUpdateDto(
        Long petId,
        String vaccineName,
        LocalDate applicationDate,
        LocalDate nextDose,
        String veterinarian,
        String batchNumber,
        String notes
) {}

