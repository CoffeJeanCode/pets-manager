package boilerplate.desktop.models.dto;

public final class UserDto {

    private UserDto() {
        throw new IllegalStateException("Utility class");
    }

    public record UserCreate(String nombre, String email) {}

    public record UserResponse(
            Integer id,
            String nombre,
            String email,
            String created_at,
            String updated_at
    ) {}
}

