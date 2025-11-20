package boilerplate.desktop.controller;

import boilerplate.desktop.models.Plant;
import boilerplate.desktop.services.PlantService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.util.List;

public class CatalogController {

    @FXML
    private FlowPane cardContainer;

    private PlantService plantService;

    @FXML
    public void initialize() {
        plantService = new PlantService();
        loadPlants();
    }

    private void loadPlants() {
        plantService.getPlants().thenAccept(plants -> {
            Platform.runLater(() -> populateCards(plants));
        }).exceptionally(throwable -> {
            System.err.println("Error loading plants: " + throwable.getMessage());
            throwable.printStackTrace();
            Platform.runLater(() -> {
                // Show empty state or error message
                cardContainer.getChildren().clear();
            });
            return null;
        });
    }

    private void populateCards(List<Plant> plants) {
        if (cardContainer == null) {
            return;
        }
        cardContainer.getChildren().clear();
        for (Plant plant : plants) {
            try {
                java.net.URL resourceUrl = getClass().getResource("/boilerplate/desktop/boilerplate/desktop/ui/PlantCard.fxml");
                if (resourceUrl == null) {
                    // Try alternative path
                    resourceUrl = getClass().getResource("/boilerplate/desktop/ui/PlantCard.fxml");
                }
                if (resourceUrl == null) {
                    throw new IllegalStateException("PlantCard.fxml not found in classpath. " +
                        "Tried: /boilerplate/desktop/boilerplate/desktop/ui/PlantCard.fxml and /boilerplate/desktop/ui/PlantCard.fxml");
                }
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                Region card = loader.load();
                PlantCardController controller = loader.getController();
                controller.setPlant(plant);
                cardContainer.getChildren().add(card);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to render plant card", e);
            }
        }
    }
}

