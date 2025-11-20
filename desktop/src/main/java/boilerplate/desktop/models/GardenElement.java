package boilerplate.desktop.models;

public record GardenElement(
        Long itemId,
        String tipo,    // planta | maceta | accesorio
        double posicionX,
        double posicionY,
        double escala
) {}

