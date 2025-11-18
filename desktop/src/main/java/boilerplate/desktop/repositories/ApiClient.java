package boilerplate.desktop.repositories;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8000/api/v1"; // Cambia si es necesario
    private final HttpClient client;
    private final Gson gson;

    public ApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public <T> CompletableFuture<List<T>> getList(String endpoint, Class<T> clazz) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException("Error HTTP " + response.statusCode() + ": " + response.body());
                    }
                    return gson.fromJson(response.body(), TypeToken.getParameterized(List.class, clazz).getType());
                });
    }

    public <T> CompletableFuture<T> getOne(String endpoint, Class<T> clazz) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() >= 400) {
                        throw new RuntimeException("Error: " + resp.body());
                    }
                    return gson.fromJson(resp.body(), clazz);
                });
    }

}