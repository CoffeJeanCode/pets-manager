package boilerplate.desktop.models.dto;

import java.time.LocalDate;

public record PetUpdateDto(
        String name,
        String species,
        String breed,
        Integer approxAge,
        LocalDate approxBirthdate,
        String sex,
        String size,
        String weight,
        String adoptionStatus,
        String healthStatus,
        String currentLocation,
        String microchip,
        String description,
        String specialNeeds,
        LocalDate intakeDate,
        String photoUrl
) {} 
