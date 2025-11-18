package boilerplate.desktop.models.dto;

import java.time.LocalDateTime;

public record AdoptionApplicationDto(
        Long id,
        Long petId,
        String applicantName,
        String applicantIdNumber,
        String email,
        String phone,
        String address,
        String housingType,
        Boolean hasOtherPets,
        String status,
        LocalDateTime applicationDate
) {} 
