package boilerplate.desktop.services;

import boilerplate.desktop.models.AdoptionApplication;
import boilerplate.desktop.models.dto.AdoptionApplicationDto;
import boilerplate.desktop.models.dto.AdoptionApplicationUpdateDto;
import boilerplate.desktop.repositories.ApiClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdoptionApplicationService {
    
    private final ApiClient apiClient;
    
    public AdoptionApplicationService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    public CompletableFuture<List<AdoptionApplication>> getAllApplications() {
        return apiClient.getList("/adoption_aplication?skip=0&limit=100", AdoptionApplication.class);
    }
    
    public CompletableFuture<List<AdoptionApplication>> getApplicationsByPetId(Long petId) {
        if (petId == null || petId <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid pet ID"));
        }
        return apiClient.getList("/adoption_aplication/pet/" + petId + "?limit=100", AdoptionApplication.class);
    }
    
    public CompletableFuture<AdoptionApplication> getApplicationById(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid application ID"));
        }
        return apiClient.getOne("/adoption_aplication/" + id, AdoptionApplication.class);
    }
    
    public CompletableFuture<AdoptionApplication> createApplication(AdoptionApplicationDto applicationDto) {
        if (applicationDto == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Application data required"));
        }
        return apiClient.post("/adoption_aplication", applicationDto, AdoptionApplication.class);
    }
    
    public CompletableFuture<AdoptionApplication> updateApplicationStatus(Long id, String status) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid application ID"));
        }
        if (status == null || status.isBlank()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Status is required"));
        }
        // Use UpdateDto with only status field set
        AdoptionApplicationUpdateDto updateDto = new AdoptionApplicationUpdateDto(
            null, null, null, null, null, null, null, null, null, null, null, status
        );
        return apiClient.put("/adoption_aplication/" + id, updateDto, AdoptionApplication.class);
    }
    
    public CompletableFuture<AdoptionApplication> updateApplication(Long id, AdoptionApplicationUpdateDto updateDto) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid application ID"));
        }
        if (updateDto == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Update data is required"));
        }
        return apiClient.put("/adoption_aplication/" + id, updateDto, AdoptionApplication.class);
    }
    
    public CompletableFuture<Void> deleteApplication(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid application ID"));
        }
        return apiClient.delete("/adoption_aplication/" + id);
    }
    
    /**
     * Obtiene el conteo total de solicitudes de adopción con filtros opcionales.
     */
    public CompletableFuture<Long> countApplications(Long petId, String status) {
        StringBuilder endpoint = new StringBuilder("/adoption_aplication/count");
        boolean hasParam = false;

        if (petId != null && petId > 0) {
            endpoint.append(hasParam ? "&" : "?").append("pet_id=").append(petId);
            hasParam = true;
        }
        if (status != null && !status.isBlank()) {
            endpoint.append(hasParam ? "&" : "?").append("status=").append(status);
        }

        return apiClient.getCount(endpoint.toString());
    }
    
    public CompletableFuture<Long> countApplications() {
        return countApplications(null, null);
    }
    
    public CompletableFuture<Long> countApplicationsByPetId(Long petId) {
        return countApplications(petId, null);
    }
    
    public CompletableFuture<Long> countApplicationsByStatus(String status) {
        return countApplications(null, status);
    }
}

