// model/Pet.java
package boilerplate.desktop.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Pet(
        Long id,
        String name,
        String species,
        String breed,
        Integer approxAge,
        LocalDate approxBirthdate,
        String sex,           // "male" | "female"
        String size,          // "small" | "medium" | "large"
        String weight,
        String adoptionStatus, // "available" | "adopted" | "in_process"
        String healthStatus,
        String currentLocation,
        String microchip,
        String description,
        String specialNeeds,
        LocalDate intakeDate,
        String photoUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public boolean isAvailable() {
        return "available".equalsIgnoreCase(adoptionStatus);
    }
}