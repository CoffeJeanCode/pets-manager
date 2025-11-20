package boilerplate.desktop.controller;

import boilerplate.desktop.services.UserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RegistrationController {

    @FXML
    private TextField nombreField;

    @FXML
    private TextField emailField;

    @FXML
    private Button registerButton;

    @FXML
    private Label errorLabel;

    private UserService userService;
    private Runnable onRegistrationSuccess;
    private java.util.function.Consumer<boilerplate.desktop.models.dto.UserDto.UserResponse> onUserRegistered;

    @FXML
    private void initialize() {
        userService = new UserService();
        errorLabel.setVisible(false);
        
        // Disable register button initially
        registerButton.setDisable(true);
        
        // Enable button when fields are filled
        nombreField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateRegisterButton();
        });
        
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateRegisterButton();
        });
    }

    public void setOnRegistrationSuccess(Runnable callback) {
        this.onRegistrationSuccess = callback;
    }
    
    public void setOnUserRegistered(java.util.function.Consumer<boilerplate.desktop.models.dto.UserDto.UserResponse> callback) {
        this.onUserRegistered = callback;
    }

    @FXML
    private void register() {
        String nombre = nombreField.getText().trim();
        String email = emailField.getText().trim();
        
        // Validate inputs
        if (nombre.isEmpty() || email.isEmpty()) {
            showError("Por favor, completa todos los campos");
            return;
        }
        
        if (!email.contains("@")) {
            showError("Por favor, ingresa un email válido");
            return;
        }
        
        // Disable button during registration
        registerButton.setDisable(true);
        errorLabel.setVisible(false);
        
        userService.registerUser(nombre, email)
            .thenAccept(userResponse -> {
                Platform.runLater(() -> {
                    if (userResponse != null) {
                        // Registration successful
                        // Notify about user registration
                        if (onUserRegistered != null) {
                            onUserRegistered.accept(userResponse);
                        }
                        // Notify about successful registration
                        if (onRegistrationSuccess != null) {
                            onRegistrationSuccess.run();
                        }
                    } else {
                        showError("No se recibió respuesta del servidor. Por favor, intente nuevamente.");
                        registerButton.setDisable(false);
                    }
                });
            })
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    String errorMessage = throwable.getMessage();
                    if (errorMessage == null && throwable.getCause() != null) {
                        errorMessage = throwable.getCause().getMessage();
                    }
                    
                    // Check if it's a connection error
                    if (errorMessage != null && errorMessage.contains("connection failed")) {
                        showError("No se pudo conectar con el servidor. Verifica que la API esté corriendo.");
                    } else if (errorMessage != null && errorMessage.contains("HTTP 400")) {
                        showError("Email ya registrado o datos inválidos. Por favor, verifica tus datos.");
                    } else {
                        showError("Error al registrar: " + 
                            (errorMessage != null ? errorMessage : "Error desconocido"));
                    }
                    registerButton.setDisable(false);
                });
                return null;
            });
    }

    private void updateRegisterButton() {
        String nombre = nombreField.getText().trim();
        String email = emailField.getText().trim();
        registerButton.setDisable(nombre.isEmpty() || email.isEmpty());
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}

