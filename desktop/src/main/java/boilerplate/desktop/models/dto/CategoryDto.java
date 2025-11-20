package boilerplate.desktop.models.dto;

public final class CategoryDto {

    private CategoryDto() {
        throw new IllegalStateException("Utility class");
    }

    public record CategoryResponse(
            Integer id,
            String name,
            String description,
            String created_at,
            String updated_at
    ) {}

    public record CategorySimple(Integer id, String name) {}
}

