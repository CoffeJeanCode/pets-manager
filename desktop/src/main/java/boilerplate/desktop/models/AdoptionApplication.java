// model/AdoptionApplication.java
package boilerplate.desktop.models;

import java.time.LocalDateTime;

public record AdoptionApplication(
        Long id,
        Long petId,
        String applicantName,
        String applicantIdNumber,
        String occupation,
        String email,
        String phone,
        String address,
        String housingType, // "house" | "apartment" | "farm"
        boolean hasOtherPets,
        String adoptionReason,
        String petExperience,
        String status,      // "pending" | "approved" | "rejected"
        LocalDateTime applicationDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}