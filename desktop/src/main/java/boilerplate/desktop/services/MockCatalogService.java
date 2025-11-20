package boilerplate.desktop.services;

import boilerplate.desktop.models.Plant;

import java.util.List;

public class MockCatalogService {

    private final List<Plant> plants = List.of(
            new Plant(1L, "Haworthia", 22, "/images/girasol.jpg",
                    4, "bajo", "Suculenta compacta ideal para rincones iluminados.", "Primavera"),
            new Plant(2L, "Hens and Chick", 24, "https://images.unsplash.com/photo-1470058869958-2a77ade41c02?auto=format&fit=crop&w=600&q=80",
                    3, "medio", "Colores suaves que combinan con espacios cálidos.", "Todo el año"),
            new Plant(3L, "Echeveria", 26, "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=600&q=80",
                    2, "bajo", "Texturas pastel para escritorios y estancias pequeñas.", "Verano"),
            new Plant(4L, "Aloe Vera", 18, "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?crop=entropy&auto=format&fit=crop&w=600&q=80",
                    5, "alto", "Purifica el aire y aporta un toque tropical.", "Verano"),
            new Plant(5L, "Monstera", 34, "https://images.unsplash.com/photo-1470058869958-2a77ade41c02?auto=format&fit=crop&w=600&q=80",
                    2, "medio", "Hojas grandes que hacen protagonista a cualquier salón.", "Invierno"),
            new Plant(6L, "Sansevieria", 28, "https://images.unsplash.com/photo-1524594081293-190a2fe0baae?auto=format&fit=crop&w=600&q=80",
                    1, "bajo", "Resistente y estilizada, ideal para principiantes.", "Todo el año")
    );

    public List<Plant> getPlants() {
        return plants;
    }
}

