package boilerplate.desktop.models.dto;

/**
 * DTOs para accesorios inteligentes.
 */
public final class AccessoryDto {

    private AccessoryDto() {
        throw new IllegalStateException("Utility class");
    }

    public record AccessoryResponse(
            Long id,
            String name,
            String feature,
            String connectivity,
            String compatibility,
            String powerUsage,
            double price,
            String imageUrl
    ) {}

    public record AccessoryCreateRequest(
            String name,
            String feature,
            String connectivity,
            String compatibility,
            String powerUsage,
            double price,
            String imageUrl
    ) {}

    public record AccessoryUpdateRequest(
            String name,
            String feature,
            String connectivity,
            String compatibility,
            String powerUsage,
            Double price,
            String imageUrl
    ) {}
}

