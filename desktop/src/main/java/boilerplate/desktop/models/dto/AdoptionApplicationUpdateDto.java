package boilerplate.desktop.models.dto;

public record AdoptionApplicationUpdateDto(
        Long petId,
        String applicantName,
        String applicantIdNumber,
        String occupation,
        String email,
        String phone,
        String address,
        String housingType,
        Boolean hasOtherPets,
        String adoptionReason,
        String petExperience,
        String status
) {}

