// src/main/java/com/refugio/service/PetService.java
package boilerplate.desktop.services;

import boilerplate.desktop.models.Pet;
import boilerplate.desktop.models.dto.PetDto;
import boilerplate.desktop.models.dto.PetUpdateDto;
import boilerplate.desktop.repositories.ApiClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio para gestionar mascotas (pets) en el refugio.
 * Encapsula toda la lógica de comunicación con la API REST.
 */
public class PetService {

    private final ApiClient apiClient;

    public PetService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Obtiene todas las mascotas disponibles para adopción.
     */
    public CompletableFuture<List<Pet>> getAvailablePets() {
        return apiClient.getList("/pets/available?limit=100", Pet.class);
    }

    /**
     * Obtiene todas las mascotas (disponibles, adoptadas, en proceso).
     * Soporta paginación.
     */
    public CompletableFuture<List<Pet>> getAllPets(int skip, int limit) {
        String endpoint = String.format("/pets?skip=%d&limit=%d", skip, limit);
        return apiClient.getList(endpoint, Pet.class);
    }

    public CompletableFuture<List<Pet>> getAllPets() {
        return getAllPets(0, 100);
    }

    /**
     * Busca una mascota por su ID.
     */
    public CompletableFuture<Pet> getPetById(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("ID de mascota inválido"));
        }
        return apiClient.getOne("/pets/" + id, Pet.class);
    }

    /**
     * Crea una nueva mascota en el refugio.
     */
    public CompletableFuture<Pet> createPet(PetDto petDto) {
        if (petDto == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Datos de mascota requeridos"));
        }
        return apiClient.post("/pets", petDto, Pet.class);
    }

    /**
     * Actualiza una mascota existente.
     * No permite cambiar el estado de una mascota ya adoptada.
     */
    public CompletableFuture<Pet> updatePet(Long id, PetUpdateDto petUpdateDto) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("ID de mascota inválido"));
        }
        if (petUpdateDto == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Datos de actualización requeridos"));
        }
        return apiClient.put("/pets/" + id, petUpdateDto, Pet.class);
    }

    /**
     * Elimina una mascota.
     * No permite eliminar si tiene solicitudes de adopción activas.
     */
    public CompletableFuture<Void> deletePet(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("ID de mascota inválido"));
        }
        return apiClient.delete("/pets/" + id);
    }

    /**
     * Obtiene el conteo total de mascotas con filtros opcionales.
     */
    public CompletableFuture<Long> countPets(String species, String adoptionStatus) {
        StringBuilder endpoint = new StringBuilder("/pets/count");
        boolean hasParam = false;

        if (species != null && !species.isBlank()) {
            endpoint.append(hasParam ? "&" : "?").append("species=").append(species);
            hasParam = true;
        }
        if (adoptionStatus != null && !adoptionStatus.isBlank()) {
            endpoint.append(hasParam ? "&" : "?").append("adoption_status=").append(adoptionStatus);
        }

        return apiClient.getOne(endpoint.toString(), Long.class)
                .thenApply(count -> count != null ? count : 0L);
    }

    public CompletableFuture<Long> countPets() {
        return countPets(null, null);
    }

    public CompletableFuture<Long> countAvailablePets() {
        return countPets(null, "available");
    }

    public CompletableFuture<Long> countAdoptedPets() {
        return countPets(null, "adopted");
    }

    /**
     * Filtra mascotas por especie y/o estado.
     */
    public CompletableFuture<List<Pet>> filterPets(String species, String adoptionStatus, int skip, int limit) {
        StringBuilder endpoint = new StringBuilder("/pets?");
        endpoint.append("skip=").append(skip)
                .append("&limit=").append(limit);

        if (species != null && !species.isBlank()) {
            endpoint.append("&species=").append(species);
        }
        if (adoptionStatus != null && !adoptionStatus.isBlank()) {
            endpoint.append("&adoption_status=").append(adoptionStatus);
        }

        return apiClient.getList(endpoint.toString(), Pet.class);
    }

    /**
     * Cambia el estado de adopción de una mascota (usado internamente al aprobar adopción).
     */
    public CompletableFuture<Pet> setAdoptionStatus(Long petId, String newStatus) {
        return getPetById(petId).thenCompose(pet -> {
            PetUpdateDto petUpdateDto = new PetUpdateDto(
                    null, null, null, null, null, null, null, null,
                    newStatus, null, null, null, null, null, null, null
            );
            return updatePet(petId, petUpdateDto);
        });
    }
}