package boilerplate.desktop.models.dto;
import java.time.LocalDate;

public record VaccineDto(
        Long id,
        Long petId,
        String vaccineName,
        LocalDate applicationDate,
        LocalDate nextDose
) {}