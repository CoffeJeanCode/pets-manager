package boilerplate.desktop.controller;

import boilerplate.desktop.models.dto.UserDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainController {

    @FXML
    private StackPane contentContainer;

    @FXML
    private ToggleButton catalogToggle;

    @FXML
    private ToggleButton designerToggle;

    @FXML
    private Label greetingLabel;
    
    @FXML
    private Button accountButton;

    private final Map<String, Node> viewCache = new HashMap<>();
    private UserDto.UserResponse currentUser;
    private boolean isRegistered = false;

    @FXML
    private void initialize() {
        greetingLabel.setText("EcoGarden");
        
        // Disable navigation buttons until user registers
        catalogToggle.setDisable(true);
        designerToggle.setDisable(true);
        
        // Hide account button until registered
        if (accountButton != null) {
            accountButton.setVisible(false);
        }
        
        // Show registration view first
        showRegistration();
    }
    
    private void showRegistration() {
        try {
            java.net.URL resourceUrl = getClass().getResource("/boilerplate/desktop/boilerplate/desktop/ui/RegistrationView.fxml");
            if (resourceUrl == null) {
                resourceUrl = getClass().getResource("/boilerplate/desktop/ui/RegistrationView.fxml");
            }
            if (resourceUrl == null) {
                throw new IllegalStateException("RegistrationView.fxml not found in classpath");
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Node registrationView = loader.load();
            RegistrationController controller = loader.getController();
            controller.setOnRegistrationSuccess(this::onRegistrationSuccess);
            controller.setOnUserRegistered(this::setCurrentUser);
            contentContainer.getChildren().setAll(registrationView);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load registration view", e);
        }
    }
    
    private void onRegistrationSuccess() {
        // Mark as registered
        isRegistered = true;
        
        // Enable navigation buttons
        catalogToggle.setDisable(false);
        designerToggle.setDisable(false);
        
        // Show account button
        if (accountButton != null) {
            accountButton.setVisible(true);
        }
        
        // Show catalog by default
        catalogToggle.setSelected(true);
        showCatalog();
        
        // Update greeting with user name if available
        if (currentUser != null && currentUser.nombre() != null) {
            greetingLabel.setText("Bienvenido, " + currentUser.nombre());
        }
    }
    
    public void setCurrentUser(UserDto.UserResponse user) {
        this.currentUser = user;
        if (user != null && user.nombre() != null) {
            greetingLabel.setText("Bienvenido, " + user.nombre());
        }
    }

    @FXML
    private void showRegistrationDialog() {
        // If not registered, show registration view
        if (!isRegistered) {
            showRegistration();
        } else {
            // If already registered, just show a message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Usuario Registrado");
            alert.setHeaderText(null);
            if (currentUser != null) {
                alert.setContentText("Ya estás registrado como: " + currentUser.nombre() + 
                    "\nEmail: " + currentUser.email());
            } else {
                alert.setContentText("Ya estás registrado");
            }
            alert.showAndWait();
        }
    }

    @FXML
    private void showCatalog() {
        if (!isRegistered) {
            showRegistration();
            return;
        }
        showView("catalog", "/boilerplate/desktop/boilerplate/desktop/ui/CatalogView.fxml", loader -> {
            CatalogController controller = loader.getController();
            controller.initialize();
        });
    }

    @FXML
    private void showDesigner() {
        if (!isRegistered) {
            showRegistration();
            return;
        }
        showView("designer", "/boilerplate/desktop/boilerplate/desktop/ui/DesignerView.fxml", loader -> {
            // GardenDesignerController initializes itself via @FXML initialize()
        });
    }

    private void showView(String key,
                          String resource,
                          ViewConfigurator configurator) {
        try {
            Node view = viewCache.computeIfAbsent(key, k -> {
                try {
                    java.net.URL resourceUrl = getClass().getResource(resource);
                    if (resourceUrl == null) {
                        throw new IllegalStateException("Resource not found: " + resource + 
                            ". Make sure the FXML file exists in the classpath.");
                    }
                    FXMLLoader loader = new FXMLLoader(resourceUrl);
                    Node loadedView = loader.load();
                    configurator.configure(loader);
                    return loadedView;
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to load view " + resource, e);
                }
            });

            contentContainer.getChildren().setAll(view);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to render view " + key, e);
        }
    }

    @FunctionalInterface
    private interface ViewConfigurator {
        void configure(FXMLLoader loader) throws IOException;
    }
}

