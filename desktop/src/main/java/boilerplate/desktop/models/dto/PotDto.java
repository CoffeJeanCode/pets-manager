package boilerplate.desktop.models.dto;

/**
 * DTOs para el catálogo de macetas.
 */
public final class PotDto {

    private PotDto() {
        throw new IllegalStateException("Utility class");
    }

    public record PotResponse(
            Long id,
            String name,
            String material,
            String size,
            String color,
            boolean drainage,
            double price,
            String imageUrl
    ) {}

    public record PotCreateRequest(
            String name,
            String material,
            String size,
            String color,
            boolean drainage,
            double price,
            String imageUrl
    ) {}

    public record PotUpdateRequest(
            String name,
            String material,
            String size,
            String color,
            Boolean drainage,
            Double price,
            String imageUrl
    ) {}
}

