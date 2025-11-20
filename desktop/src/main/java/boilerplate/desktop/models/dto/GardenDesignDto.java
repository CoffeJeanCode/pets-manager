package boilerplate.desktop.models.dto;

import java.util.List;

public final class GardenDesignDto {

    private GardenDesignDto() {
        throw new IllegalStateException("Utility class");
    }

    public record GardenElementDto(
            Long itemId,
            String tipo,
            double posicionX,
            double posicionY,
            double escala
    ) {}

    public record GardenDesignResponse(
            Long id,
            String nombre,
            List<GardenElementDto> elementos
    ) {}

    public record GardenDesignSaveRequest(
            String nombre,
            List<GardenElementDto> elementos
    ) {}
}

