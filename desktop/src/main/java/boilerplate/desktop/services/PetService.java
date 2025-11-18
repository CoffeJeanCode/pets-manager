// service/PetService.java
package boilerplate.desktop.services;

import boilerplate.desktop.models.Pet;
import boilerplate.desktop.repositories.ApiClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PetService {
    private final ApiClient apiClient;

    public PetService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<List<Pet>> getAvailablePets() {
        return apiClient.getList("/pets/available?limit=100", Pet.class);
    }

    public CompletableFuture<List<Pet>> getAllPets() {
        return apiClient.getList("/pets?limit=100", Pet.class);
    }

    public CompletableFuture<Pet> getPetById(Long id) {
        return apiClient.getOne("/pets/" + id, Pet.class);
    }

    // Más métodos: crear, actualizar, eliminar, contar...
}