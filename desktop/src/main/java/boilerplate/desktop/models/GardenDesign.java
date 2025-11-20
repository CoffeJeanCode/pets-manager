package boilerplate.desktop.models;

import java.time.LocalDateTime;
import java.util.List;

public record GardenDesign(
        Long id,
        String nombre,
        List<GardenElement> elementos,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

