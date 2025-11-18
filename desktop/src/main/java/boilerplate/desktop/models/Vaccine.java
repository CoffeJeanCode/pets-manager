// model/Vaccine.java
package boilerplate.desktop.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Vaccine(
        Long id,
        Long petId,
        String vaccineName,
        LocalDate applicationDate,
        LocalDate nextDose,
        String veterinarian,
        String batchNumber,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}