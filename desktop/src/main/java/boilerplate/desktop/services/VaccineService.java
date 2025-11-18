package boilerplate.desktop.services;

import boilerplate.desktop.models.Vaccine;
import boilerplate.desktop.models.dto.VaccineDto;
import boilerplate.desktop.models.dto.VaccineUpdateDto;
import boilerplate.desktop.repositories.ApiClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VaccineService {
    
    private final ApiClient apiClient;
    
    public VaccineService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    public CompletableFuture<List<Vaccine>> getAllVaccines() {
        return apiClient.getList("/vaccines?limit=100", Vaccine.class);
    }
    
    public CompletableFuture<List<Vaccine>> getVaccinesByPetId(Long petId) {
        if (petId == null || petId <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid pet ID"));
        }
        return apiClient.getList("/vaccines/pet/" + petId + "?limit=100", Vaccine.class);
    }
    
    public CompletableFuture<Vaccine> getVaccineById(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid vaccine ID"));
        }
        return apiClient.getOne("/vaccines/" + id, Vaccine.class);
    }
    
    public CompletableFuture<Vaccine> createVaccine(VaccineDto vaccineDto) {
        if (vaccineDto == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Vaccine data required"));
        }
        return apiClient.post("/vaccines", vaccineDto, Vaccine.class);
    }
    
    public CompletableFuture<Vaccine> updateVaccine(Long id, VaccineUpdateDto vaccineDto) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid vaccine ID"));
        }
        if (vaccineDto == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Vaccine data required"));
        }
        return apiClient.put("/vaccines/" + id, vaccineDto, Vaccine.class);
    }
    
    public CompletableFuture<Void> deleteVaccine(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid vaccine ID"));
        }
        return apiClient.delete("/vaccines/" + id);
    }
    
    /**
     * Obtiene el conteo total de vacunas con filtro opcional por pet_id.
     */
    public CompletableFuture<Long> countVaccines(Long petId) {
        if (petId != null && petId > 0) {
            return apiClient.getCount("/vaccines/count?pet_id=" + petId);
        }
        return apiClient.getCount("/vaccines/count");
    }
    
    public CompletableFuture<Long> countVaccines() {
        return countVaccines(null);
    }
}

