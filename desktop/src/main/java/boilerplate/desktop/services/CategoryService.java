package boilerplate.desktop.services;

import boilerplate.desktop.models.dto.CategoryDto;
import boilerplate.desktop.repositories.ApiClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CategoryService {

    private final ApiClient apiClient;

    public CategoryService() {
        this.apiClient = new ApiClient();
    }

    public CompletableFuture<List<CategoryDto.CategoryResponse>> getCategories() {
        return apiClient.getList("/category", CategoryDto.CategoryResponse.class);
    }
}

