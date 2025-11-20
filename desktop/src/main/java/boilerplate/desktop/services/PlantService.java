package boilerplate.desktop.services;

import boilerplate.desktop.models.Plant;
import boilerplate.desktop.repositories.ApiClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlantService {

    private final ApiClient apiClient;

    public PlantService() {
        this.apiClient = new ApiClient();
    }

    public CompletableFuture<List<Plant>> getPlants() {
        return apiClient.getList("/plant", Plant.class);
    }

    public CompletableFuture<List<Plant>> filterPlants(String luz, String riego, String ambiente, Integer categoriaId) {
        StringBuilder endpoint = new StringBuilder("/plant/filter?");
        boolean hasParam = false;

        if (luz != null && !luz.isEmpty()) {
            endpoint.append("luz=").append(luz);
            hasParam = true;
        }
        if (riego != null && !riego.isEmpty()) {
            if (hasParam) endpoint.append("&");
            endpoint.append("riego=").append(riego);
            hasParam = true;
        }
        if (ambiente != null && !ambiente.isEmpty()) {
            if (hasParam) endpoint.append("&");
            endpoint.append("ambiente=").append(ambiente);
            hasParam = true;
        }
        if (categoriaId != null) {
            if (hasParam) endpoint.append("&");
            endpoint.append("categoria_id=").append(categoriaId);
        }

        return apiClient.getList(endpoint.toString(), Plant.class);
    }
}

