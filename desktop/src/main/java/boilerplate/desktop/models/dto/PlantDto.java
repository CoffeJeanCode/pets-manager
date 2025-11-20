package boilerplate.desktop.models.dto;

import java.util.List;

/**
 * DTOs relacionados con plantas dentro de EcoGallery.
 * Se agrupan en un único archivo para mantener la convención del proyecto.
 */
public final class PlantDto {

    private PlantDto() {
        throw new IllegalStateException("Utility class");
    }

    public record PlantResponse(
            Long id,
            String name,
            int lightLevel,
            String wateringType,
            double price,
            String description,
            String imageUrl,
            List<String> tags
    ) {}

    public record PlantCreateRequest(
            String name,
            int lightLevel,
            String wateringType,
            double price,
            String description,
            String imageUrl,
            List<String> tags
    ) {}

    public record PlantUpdateRequest(
            String name,
            Integer lightLevel,
            String wateringType,
            Double price,
            String description,
            String imageUrl,
            List<String> tags
    ) {}
}

