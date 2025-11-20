// src/main/java/boilerplate/desktop/repositories/ApiClient.java
package boilerplate.desktop.repositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8003/api/v1";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient client;
    private final Gson gson;

    public ApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setLenient()
                .create();
    }

    // ==================== GET ====================

    public <T> CompletableFuture<List<T>> getList(String endpoint, Class<T> clazz) {
        return getList(endpoint, clazz, null);
    }

    public <T> CompletableFuture<List<T>> getList(String endpoint, Class<T> clazz, String authToken) {
        HttpRequest request = buildRequest(endpoint, authToken)
                .GET()
                .build();

        return sendAsync(request)
                .thenApply(response -> {
                    try {
                        return parseList(response, clazz);
                    } catch (Exception e) {
                        System.err.println("Error parsing list from " + endpoint + ": " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException("Failed to parse response from " + endpoint, e);
                    }
                })
                .exceptionally(throwable -> {
                    // Suppress stack traces for connection errors (API not available)
                    Throwable cause = throwable.getCause();
                    boolean isConnectionError = cause instanceof java.net.ConnectException || 
                                              cause instanceof java.nio.channels.ClosedChannelException ||
                                              (throwable instanceof java.util.concurrent.CompletionException && 
                                               throwable.getCause() instanceof java.net.ConnectException);
                    
                    if (isConnectionError) {
                        // Only show connection error once per endpoint to avoid spam
                        System.err.println("API connection failed for " + endpoint + ". Make sure the API is running on localhost:8003");
                    } else {
                        System.err.println("Error in getList for " + endpoint + ": " + throwable.getMessage());
                        if (cause != null && !(cause instanceof java.net.ConnectException)) {
                            cause.printStackTrace();
                        }
                    }
                    return List.of(); // Return empty list on error
                });
    }

    public <T> CompletableFuture<T> getOne(String endpoint, Class<T> clazz) {
        return getOne(endpoint, clazz, null);
    }

    public <T> CompletableFuture<T> getOne(String endpoint, Class<T> clazz, String authToken) {
        HttpRequest request = buildRequest(endpoint, authToken)
                .GET()
                .build();

        return sendAsync(request)
                .thenApply(response -> parseOne(response, clazz))
                .exceptionally(throwable -> {
                    // Suppress stack traces for connection errors (API not available)
                    Throwable cause = throwable.getCause();
                    boolean isConnectionError = cause instanceof java.net.ConnectException || 
                                              cause instanceof java.nio.channels.ClosedChannelException ||
                                              (throwable instanceof java.util.concurrent.CompletionException && 
                                               throwable.getCause() instanceof java.net.ConnectException);
                    
                    if (isConnectionError) {
                        System.err.println("API connection failed for " + endpoint + ". Make sure the API is running on localhost:8003");
                    } else {
                        System.err.println("Error in getOne for " + endpoint + ": " + throwable.getMessage());
                        if (cause != null && !(cause instanceof java.net.ConnectException)) {
                            cause.printStackTrace();
                        }
                    }
                    return null;
                });
    }

    // ==================== POST ====================

    public <T, R> CompletableFuture<R> post(String endpoint, T body, Class<R> responseType) {
        return post(endpoint, body, responseType, null);
    }

    public <T, R> CompletableFuture<R> post(String endpoint, T body, Class<R> responseType, String authToken) {
        if (body == null) {
            throw new IllegalArgumentException("Request body cannot be null for POST to " + endpoint);
        }
        
        String json = gson.toJson(body);
        // Debug: log the JSON being sent
        System.out.println("POST to " + BASE_URL + endpoint + " with body: " + json);
        
        if (json == null || json.trim().isEmpty() || json.equals("null") || json.equals("{}")) {
            throw new RuntimeException("Cannot send POST request: serialized body is null or empty. Original body: " + body + ", JSON: " + json);
        }
        
        // Build request with Content-Type header before POST method
        HttpRequest.Builder requestBuilder = buildRequest(endpoint, authToken)
                .header("Content-Type", "application/json; charset=utf-8");
        
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(json, java.nio.charset.StandardCharsets.UTF_8))
                .build();

        return sendAsync(request)
                .thenApply(response -> {
                    if (response.statusCode() == 201 || response.statusCode() == 200) {
                        return parseOne(response, responseType);
                    }
                    throw handleError(response);
                })
                .exceptionally(throwable -> {
                    // Suppress stack traces for connection errors (API not available)
                    Throwable cause = throwable.getCause();
                    boolean isConnectionError = cause instanceof java.net.ConnectException || 
                                              cause instanceof java.nio.channels.ClosedChannelException ||
                                              (throwable instanceof java.util.concurrent.CompletionException && 
                                               throwable.getCause() instanceof java.net.ConnectException);
                    
                    if (isConnectionError) {
                        System.err.println("API connection failed for " + endpoint + ". Make sure the API is running on localhost:8003");
                    } else {
                        // Log error but don't print full stack trace for HTTP errors
                        String errorMsg = throwable.getMessage();
                        if (errorMsg != null && errorMsg.contains("HTTP")) {
                            System.err.println("API error for " + endpoint + ": " + errorMsg);
                        } else {
                            System.err.println("Error in post for " + endpoint + ": " + errorMsg);
                            if (cause != null && !(cause instanceof java.net.ConnectException)) {
                                cause.printStackTrace();
                            }
                        }
                    }
                    return null;
                });
    }

    // ==================== PUT ====================

    public <T, R> CompletableFuture<R> put(String endpoint, T body, Class<R> responseType) {
        return put(endpoint, body, responseType, null);
    }

    public <T, R> CompletableFuture<R> put(String endpoint, T body, Class<R> responseType, String authToken) {
        if (body == null) {
            throw new IllegalArgumentException("Request body cannot be null for PUT to " + endpoint);
        }
        
        String json = gson.toJson(body);
        System.out.println("PUT to " + BASE_URL + endpoint + " with body: " + json);
        
        if (json == null || json.trim().isEmpty() || json.equals("null") || json.equals("{}")) {
            throw new RuntimeException("Cannot send PUT request: serialized body is null or empty. Original body: " + body + ", JSON: " + json);
        }
        
        // Build request with Content-Type header before PUT method
        HttpRequest.Builder requestBuilder = buildRequest(endpoint, authToken)
                .header("Content-Type", "application/json; charset=utf-8");
        
        HttpRequest request = requestBuilder
                .PUT(HttpRequest.BodyPublishers.ofString(json, java.nio.charset.StandardCharsets.UTF_8))
                .build();

        return sendAsync(request)
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return parseOne(response, responseType);
                    }
                    throw handleError(response);
                })
                .exceptionally(throwable -> {
                    // Suppress stack traces for connection errors (API not available)
                    Throwable cause = throwable.getCause();
                    boolean isConnectionError = cause instanceof java.net.ConnectException || 
                                              cause instanceof java.nio.channels.ClosedChannelException ||
                                              (throwable instanceof java.util.concurrent.CompletionException && 
                                               throwable.getCause() instanceof java.net.ConnectException);
                    
                    if (isConnectionError) {
                        System.err.println("API connection failed for " + endpoint + ". Make sure the API is running on localhost:8003");
                    } else {
                        String errorMsg = throwable.getMessage();
                        if (errorMsg != null && errorMsg.contains("HTTP")) {
                            System.err.println("API error for " + endpoint + ": " + errorMsg);
                        } else {
                            System.err.println("Error in put for " + endpoint + ": " + errorMsg);
                            if (cause != null && !(cause instanceof java.net.ConnectException)) {
                                cause.printStackTrace();
                            }
                        }
                    }
                    return null;
                });
    }

    // ==================== DELETE ====================

    public CompletableFuture<Void> delete(String endpoint) {
        return delete(endpoint, null);
    }

    public CompletableFuture<Void> delete(String endpoint, String authToken) {
        HttpRequest request = buildRequest(endpoint, authToken)
                .DELETE()
                .build();

        return sendAsync(request)
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        return null;
                    }
                    throw handleError(response);
                });
    }

    // ==================== UTILIDADES PRIVADAS ====================

    private HttpRequest.Builder buildRequest(String endpoint, String authToken) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .timeout(TIMEOUT)
                .header("Accept", "application/json");

        if (authToken != null && !authToken.isBlank()) {
            builder.header("Authorization", "Bearer " + authToken);
        }

        return builder;
    }

    private CompletableFuture<HttpResponse<String>> sendAsync(HttpRequest request) {
        return client.sendAsync(request, BodyHandlers.ofString());
    }

    private <T> List<T> parseList(HttpResponse<String> response, Class<T> clazz) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw handleError(response);
        }
        String body = response.body();
        if (body == null || body.trim().isEmpty() || body.trim().equals("null")) {
            return List.of();
        }
        try {
            // Handle empty array
            if (body.trim().equals("[]")) {
                return List.of();
            }
            return gson.fromJson(body,
                    TypeToken.getParameterized(List.class, clazz).getType());
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + body);
            e.printStackTrace();
            throw new RuntimeException("Error parsing JSON response: " + e.getMessage() + "\nBody: " + body, e);
        }
    }

    private <T> T parseOne(HttpResponse<String> response, Class<T> clazz) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw handleError(response);
        }
        String body = response.body();
        if (body == null || body.trim().isEmpty() || body.trim().equals("null")) {
            return null;
        }
        try {
            return gson.fromJson(body, clazz);
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + body);
            e.printStackTrace();
            throw new RuntimeException("Error parsing JSON response: " + e.getMessage() + "\nBody: " + body, e);
        }
    }

    private RuntimeException handleError(HttpResponse<String> response) {
        String body = response.body() != null ? response.body() : "Sin cuerpo";
        
        // Provide more helpful error messages based on status code
        if (response.statusCode() == 400) {
            return new RuntimeException("Error en API: Solicitud inválida (HTTP 400) - " + body);
        } else if (response.statusCode() == 404) {
            return new RuntimeException("Error en API: Recurso no encontrado (HTTP 404) - " + response.uri());
        } else if (response.statusCode() == 422) {
            return new RuntimeException("Error en API: Error de validación (HTTP 422) - " + body);
        } else if (response.statusCode() == 500) {
            return new RuntimeException("Error en API: Error interno del servidor (HTTP 500) - " + body);
        }
        
        return new RuntimeException("Error en API: HTTP " + response.statusCode() + " - " + response.uri() + ": " + body);
    }

    // ==================== MÉTODOS EXTRA ====================

    public CompletableFuture<Long> getCount(String endpoint) {
        return getOne(endpoint, Long.class)
                .thenApply(count -> count != null ? count : 0L);
    }

    public CompletableFuture<Double> getTotalDonations() {
        return getOne("/donations/total", Double.class)
                .thenApply(total -> total != null ? total : 0.0);
    }
}