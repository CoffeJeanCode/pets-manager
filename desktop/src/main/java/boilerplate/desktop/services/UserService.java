package boilerplate.desktop.services;

import boilerplate.desktop.models.dto.UserDto;
import boilerplate.desktop.repositories.ApiClient;

import java.util.concurrent.CompletableFuture;

public class UserService {

    private final ApiClient apiClient;

    public UserService() {
        this.apiClient = new ApiClient();
    }

    public CompletableFuture<UserDto.UserResponse> registerUser(String nombre, String email) {
        UserDto.UserCreate userCreate = new UserDto.UserCreate(nombre, email);
        return apiClient.post("/users", userCreate, UserDto.UserResponse.class);
    }
}

