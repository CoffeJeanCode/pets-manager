package boilerplate.desktop.models.dto;

import java.util.List;

public final class RecommendationDto {

    private RecommendationDto() {
        throw new IllegalStateException("Utility class");
    }

    public record RecommendationRequest(int nivelLuz) {}

    public record RecommendationResponse(
            int nivelSeleccionado,
            List<PlantDto.PlantResponse> plantasRecomendadas
    ) {}
}

